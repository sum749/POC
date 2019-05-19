package com.tr.fuzzymatcher.function;

import com.tr.fuzzymatcher.domain.Element;
import com.tr.fuzzymatcher.domain.Token;
import com.tr.fuzzymatcher.util.Utils;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A functional interface to Tokenize Elements
 */
public interface TokenizerFunction extends Function<Element, Stream<Token>> {

    static TokenizerFunction valueTokenizer() {
        return (element -> Stream.of(getToken(element, element.getPreProcessedValue(), false)));
    }

    static TokenizerFunction wordTokenizer() {
        return (element) -> Arrays.stream(element.getPreProcessedValue().toString().split("\\s+"))
                .map(token -> getToken(element, token, false));
    }

    static TokenizerFunction triGramTokenizer() {
        return (element) -> getTokens(3, element);
    }

    static TokenizerFunction decaGramTokenizer() {
        return (element) -> getTokens(10, element);
    }


    static Stream<Token> getTokens(int size, Element element) {
        Object elementValue = element.getPreProcessedValue();
        return Utils.getNGrams(elementValue, size)
                .map(str -> getToken(element, str, true));

    }

    static Token getToken(Element element, Object token, boolean nGramTokenized) {
        return new Token(token, element, nGramTokenized);
    }
}
