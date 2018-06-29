package com.json.demo;

import com.json.demo.parser.Parser;
import com.json.demo.tokenizer.ReaderChar;
import com.json.demo.tokenizer.TokenList;
import com.json.demo.tokenizer.Tokenizer;

import java.io.IOException;
import java.io.StringReader;

public class JSONParser {
    private Tokenizer tokenizer = new Tokenizer();

    private Parser parser = new Parser();

    public Object fromJSON(String json) throws IOException {
        ReaderChar charReader = new ReaderChar(new StringReader(json));
        TokenList tokens = tokenizer.getTokenStream(charReader);
        return parser.parse(tokens);
    }
}
