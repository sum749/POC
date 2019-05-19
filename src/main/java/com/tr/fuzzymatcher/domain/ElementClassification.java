package com.tr.fuzzymatcher.domain;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Defines how each element is classified using ElementType and variance.
 * <ul>
 * <li>ElementType is an enum which gives a template on all the functions that should be applied during match</li>
 * <li>Variance is a user defined String, that allows multiple ElementType to be defined in a Document</li>
 * <li>matchOptimizerFunction is a configurable function for match performance that minimizes complexity.</li>
 * </ul>
 */
public class ElementClassification {

    private ElementType elementType;

    private String variance;

    Function<List<Token>, Stream<Match<Token>>> matchOptimizerFunction;

    public ElementClassification(ElementType elementType, String variance, Function<List<Token>, Stream<Match<Token>>> matchOptimizerFunction) {
        this.elementType = elementType;
        this.variance = variance;
        this.matchOptimizerFunction = matchOptimizerFunction;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public String getVariance() {
        return variance;
    }

    public Function<List<Token>, Stream<Match<Token>>> getMatchOptimizerFunction() {
        return matchOptimizerFunction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementClassification that = (ElementClassification) o;
        return elementType == that.elementType &&
                Objects.equals(variance, that.variance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType, variance);
    }
}
