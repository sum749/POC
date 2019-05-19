package com.tr.fuzzymatcher.domain;


import static com.tr.fuzzymatcher.function.MatchOptimizerFunction.*;
import static com.tr.fuzzymatcher.function.PreProcessFunction.*;
import static com.tr.fuzzymatcher.function.SimilarityMatchFunction.*;
import static com.tr.fuzzymatcher.function.TokenizerFunction.*;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * Enum to define different types of Element.
 * This is used only to categorize the data, and apply functions at different stages of match.
 * The functions, can be overridden from Element class using the appropriate setters at the time of creation.
 */
public enum ElementType {
    NAME(namePreprocessing(), wordTokenizer(), soundex(), searchGroupOptimizer()),
    TEXT(removeSpecialChars(), wordTokenizer(), jaccard(), searchGroupOptimizer()),
    ADDRESS(addressPreprocessing(), wordTokenizer(), soundex(), searchGroupOptimizer()),
    EMAIL(removeDomain(), triGramTokenizer(),  equality(), searchGroupOptimizer()),
    PHONE(usPhoneNormalization(),decaGramTokenizer(), equality(), searchGroupOptimizer()),
    NUMBER(numberPreprocessing(),valueTokenizer(),numberDifferenceRate(), numberSortOptimizer()),
    DATE(none(),valueTokenizer(),dateDifferenceWithinYear(), dateSortOptimizer());


    private final Function<Object, Object> preProcessFunction;

    private final Function<Element, Stream<Token>> tokenizerFunction;

    private final BiFunction<Token, Token, Double> similarityMatchFunction;

    private final Function<List<Token>, Stream<Match<Token>>> matchOptimizerFunction;

    ElementType(Function<Object, Object> preProcessFunction, Function<Element, Stream<Token>> tokenizerFunction,
                BiFunction<Token, Token, Double> similarityMatchFunction, Function<List<Token>, Stream<Match<Token>>> matchOptimizerFunction) {
        this.preProcessFunction = preProcessFunction;
        this.tokenizerFunction = tokenizerFunction;
        this.similarityMatchFunction = similarityMatchFunction;
        this.matchOptimizerFunction = matchOptimizerFunction;
    }

    public Function<Object, Object> getPreProcessFunction() {
        return preProcessFunction;
    }


    public Function<Element, Stream<Token>> getTokenizerFunction() {
        return tokenizerFunction;
    }

    public BiFunction<Token, Token, Double> getSimilarityMatchFunction() {
        return similarityMatchFunction;
    }

    public Function<List<Token>, Stream<Match<Token>>> getMatchOptimizerFunction() {
        return matchOptimizerFunction;
    }
}
