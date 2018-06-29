package com.json.demo.tokenizer;

import com.json.demo.exception.JsonParseException;

import java.io.IOException;

/**
 * 词法解析
 */
public class Tokenizer {
    private ReaderChar readerChar;
    private TokenList tokenList;

    public TokenList getTokenStream(ReaderChar readerChar) throws IOException {
        this.readerChar = readerChar;
        tokenList = new TokenList();

        // 词法解析，获取token流
        tokenizer();

        return tokenList;
    }

    /**
     * 将JSON文件解析成token流
     * @throws IOException
     */
    private void tokenizer() throws IOException {
        Token token;
        do {
            token = start();
            tokenList.add(token);
        } while (token.getTokenType() != TokenType.END_DOCUMENT);
    }

    /**
     * 解析过程的具体实现方法
     * @return
     * @throws IOException
     * @throws JsonParseException
     */
    private Token start() throws IOException, JsonParseException {
        char ch;
        while (true){   //先读一个字符，若为空白符（ASCII码在[0, 20H]上）则接着读，直到刚读的字符非空白符
            if (!readerChar.hasMore()) {
                return new Token(TokenType.END_DOCUMENT, null);
            }

            ch = readerChar.next();
            if (!isWhiteSpace(ch)) {
                break;
            }
        }

        switch (ch) {
            case '{':
                return new Token(TokenType.BEGIN_OBJECT, String.valueOf(ch));
            case '}':
                return new Token(TokenType.END_OBJECT, String.valueOf(ch));
            case '[':
                return new Token(TokenType.BEGIN_ARRAY, String.valueOf(ch));
            case ']':
                return new Token(TokenType.END_ARRAY, String.valueOf(ch));
            case ',':
                return new Token(TokenType.SEP_COMMA, String.valueOf(ch));
            case ':':
                return new Token(TokenType.SEP_COLON, String.valueOf(ch));
            case 'n':
                return readNull();
            case 't':
            case 'f':
                return readBoolean();
            case '"':
                return readString();
            case '-':
                return readNumber();
        }

        if (isDigit(ch)) {
            return readNumber();
        }

        throw new JsonParseException("Illegal character");
    }

    /**       以下方法用来判断所属数据类型是否合法          */

    // 判断一个字符是否属于空白字符
    private boolean isWhiteSpace(char ch) {
        return (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n');
    }

    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while(true) {
            char ch = readerChar.next();
            if (ch == '\\') {   // 处理转义字符
                if (!isEscape()) {
                    throw new JsonParseException("Invalid escape character");
                }
                sb.append('\\');
                ch = readerChar.peek();
                sb.append(ch);
                if (ch == 'u') {   // 处理 Unicode 编码，形如 \u4e2d。且只支持 \u0000 ~ \uFFFF 范围内的编码
                    for (int i = 0; i < 4; i++) {
                        ch = readerChar.next();
                        if (isHex(ch)) {
                            sb.append(ch);
                        } else {
                            throw new JsonParseException("Invalid character");
                        }
                    }
                }
            } else if (ch == '"') {     // 碰到另一个双引号，则认为字符串解析结束，返回 Token
                return new Token(TokenType.STRING, sb.toString());
            } else if (ch == '\r' || ch == '\n') {     // 传入的 JSON 字符串不允许换行
                throw new JsonParseException("Invalid character");
            } else {
                sb.append(ch);
            }
        }
    }

    /**
     * 判断是否有乱传转义字符
     * @return
     * @throws IOException
     */
    private boolean isEscape() throws IOException {
        char ch = readerChar.next();
        return (ch == '"' || ch == '\\' || ch == 'u' || ch == 'r'
                || ch == 'n' || ch == 'b' || ch == 't' || ch == 'f' || ch == '/');
    }

    /**
     * 判断是否是十六进制数
     * @param ch
     * @return
     */
    private boolean isHex(char ch) {
        return ((ch >= '0' && ch <= '9') || ('a' <= ch && ch <= 'f')
                || ('A' <= ch && ch <= 'F'));
    }

    /**
     * 判断是否是整数
     * @return
     * @throws IOException
     */
    private Token readNumber() throws IOException {
        char ch = readerChar.peek();
        StringBuilder sb = new StringBuilder();
        if (ch == '-') {    // 处理负数
            sb.append(ch);
            ch = readerChar.next();
            if (ch == '0') {    // 处理 -0.xxxx
                sb.append(ch);
                sb.append(readFracAndExp());
            } else if (isDigitOneToNine(ch)) {
                do {
                    sb.append(ch);
                    ch = readerChar.next();
                } while (isDigit(ch));
                if (ch != (char) -1) {
                    readerChar.back();
                    sb.append(readFracAndExp());
                }
            } else {
                throw new JsonParseException("Invalid minus number");
            }
        } else if (ch == '0') {    // 处理小数
            sb.append(ch);
            sb.append(readFracAndExp());
        } else {
            do {
                sb.append(ch);
                ch = readerChar.next();
            } while (isDigit(ch));
            if (ch != (char) -1) {
                readerChar.back();
                sb.append(readFracAndExp());
            }
        }

        return new Token(TokenType.NUMBER, sb.toString());
    }

    /**
     * 判断是否是指数
     * @param ch
     * @return
     * @throws IOException
     */
    private boolean isExp(char ch) throws IOException {
        return ch == 'e' || ch == 'E';
    }

    /**
     * 判断范围[0,9]
     * @param ch
     * @return
     */
    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * 判断范围[1,9]
     * @param ch
     * @return
     */
    private boolean isDigitOneToNine(char ch) {
        return ch >= '1' && ch <= '9';
    }


    private String readFracAndExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = readerChar.next();
        if (ch ==  '.') {
            sb.append(ch);
            ch = readerChar.next();
            if (!isDigit(ch)) {
                throw new JsonParseException("Invalid frac");
            }
            do {
                sb.append(ch);
                ch = readerChar.next();
            } while (isDigit(ch));

            if (isExp(ch)) {    // 处理科学计数法
                sb.append(ch);
                sb.append(readExp());
            } else {
                if (ch != (char) -1) {
                    readerChar.back();
                }
            }
        } else if (isExp(ch)) {
            sb.append(ch);
            sb.append(readExp());
        } else {
            readerChar.back();
        }

        return sb.toString();
    }

    /**
     * 处理指数形式的数据
     * @return
     * @throws IOException
     */
    private String readExp() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = readerChar.next();
        if (ch == '+' || ch =='-') {
            sb.append(ch);
            ch = readerChar.next();
            if (isDigit(ch)) {
                do {
                    sb.append(ch);
                    ch = readerChar.next();
                } while (isDigit(ch));

                if (ch != (char) -1) {    // 读取结束，不用回退
                    readerChar.back();
                }
            } else {
                throw new JsonParseException("e or E");
            }
        } else {
            throw new JsonParseException("e or E");
        }

        return sb.toString();
    }

    /**
     * 判断是否是true or false
     * @return
     * @throws IOException
     */
    private Token readBoolean() throws IOException {
        if (readerChar.peek() == 't') {
            if (!(readerChar.next() == 'r' && readerChar.next() == 'u' && readerChar.next() == 'e')) {
                throw new JsonParseException("Invalid json string");
            }

            return new Token(TokenType.BOOLEAN, "true");
        } else {
            if (!(readerChar.next() == 'a' && readerChar.next() == 'l'
                    && readerChar.next() == 's' && readerChar.next() == 'e')) {
                throw new JsonParseException("Invalid json string");
            }

            return new Token(TokenType.BOOLEAN, "false");
        }
    }

    /**
     * 词法分析器在读取字符n后，期望后面的三个字符分别是u,l,l，与 n 组成词 null。
     * 如果满足期望，则返回类型为 NULL 的 Token，否则报异常。
     */
    private Token readNull() throws IOException {
        if (!(readerChar.next() == 'u' && readerChar.next() == 'l' && readerChar.next() == 'l')) {
            throw new JsonParseException("Invalid json string");
        }

        return new Token(TokenType.NULL, "null");
    }
}
