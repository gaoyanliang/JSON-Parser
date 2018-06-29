package com.json.demo.tokenizer;

import java.io.IOException;
import java.io.Reader;

public class ReaderChar {
    private static final int BUFFER_SIZE = 1024;
    private Reader reader;
    private char[] buffer;
    private int index;      // 下标
    private int size;

    public ReaderChar(Reader reader) {
        this.reader = reader;
        buffer = new char[BUFFER_SIZE];
    }

    /**
     * 返回 pos 下标处的字符，并返回
     * @return
     */
    public char peek() {
        if (index - 1 >= size) {
            return (char) -1;
        }

        return buffer[Math.max(0, index - 1)];
    }

    /**
     * 返回 pos 下标处的字符，并将 pos + 1，最后返回字符
     * @return
     * @throws IOException
     */
    public char next() throws IOException {
        if (!hasMore()) {
            return (char) -1;
        }

        return buffer[index++];
    }

    /**
     * 下标回退
     */
    public void back() {
        index = Math.max(0, --index);
    }

    /**
     * 判断流是否结束
     */
    public boolean hasMore() throws IOException {
        if (index < size) {
            return true;
        }

        fillBuffer();
        return index < size;
    }

    /**
     * 填充buffer数组
     * @throws IOException
     */
    void fillBuffer() throws IOException {
        int n = reader.read(buffer);
        if (n == -1) {
            return;
        }

        index = 0;
        size = n;
    }
}
