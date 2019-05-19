package com.tr.fuzzymatcher.function;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tr.fuzzymatcher.domain.Match;
import com.tr.fuzzymatcher.domain.Score;

/**
 * A functional interface to get a score between 2 Match objects
 */
public interface ScoringFunction extends Function<Match, Score> {

    double EXPONENT = 1.5;
    double EXPONENTIAL_INCREASE_THRESHOLD = 0.9;
    double DEFAULT_UNMATCHED_CHILD_SCORE = 0.5;

    /**
     * For all the childScores in a Match object it calculates the average.
     * To get a balanced average for 2 Match object which do not have same number of childScores.
     * It gives a score of 0.5 for missing children
     *
     * @return the scoring function for Average
     */
    static ScoringFunction getAverageScore() {
        return match -> {
            List<Score> childScores = match.getChildScores();
            double numerator = getSumOfResult(childScores) + getUnmatchedChildScore(match);
            double denominator = getChildCount(match);
            return new Score(numerator / denominator, match);
        };
    }

    /**
     * For all the childScores in a Match object it calculates the average.
     * Average is calculated with a total of child scored divided by the child count
     *
     * @return the scoring function for Simple Average
     */
    static ScoringFunction getSimpleAverageScore() {
        return match -> {
            List<Score> childScores = match.getChildScores();
            double numerator = getSumOfResult(childScores);
            double denominator = getChildCount(match);
            return new Score(numerator / denominator, match);
        };
    }

    /**
     * Follows the same rules as "getAverageScore" and in addition applies weights to children.
     * It can be used for aggregating Elements to Documents, where weights can be provided at Element level
     *
     * @return the scoring function for WeightedAverage
     */
    static ScoringFunction getWeightedAverageScore() {
        return match -> {
            List<Score> childScoreList = match.getChildScores();
            double numerator = getSumOfWeightedResult(childScoreList)
                    + getUnmatchedChildScore(match);
            double denominator = getSumOfWeights(childScoreList)
                    + getChildCount(match)
                    - childScoreList.size();
            return new Score(numerator / denominator, match);
        };
    }

    /**
     * Follows the same rules as "getAverageScore", and in addition if more than 1 children match above a score of 0.9,
     * it exponentially increases the overall score by using a 1.5 exponent
     *
     * @return the scoring function for ExponentialAverage
     */
    static ScoringFunction getExponentialAverageScore() {
        return match -> {
            List<Score> childScoreList = match.getChildScores();
            List<Score> perfectMatchedElements = getPerfectMatchedElement(childScoreList);

            if (perfectMatchedElements.size() > 1 && getSumOfResult(perfectMatchedElements) > 1) {
                double numerator = getExponentiallyIncreasedValue(getSumOfResult(perfectMatchedElements))
                        + getSumOfResult(getNonPerfectMatchedElement(childScoreList))
                        + getUnmatchedChildScore(match);

                double denominator = getExponentiallyIncreasedValue(perfectMatchedElements.size())
                        + getChildCount(match)
                        - perfectMatchedElements.size();
                return new Score(numerator / denominator, match);
            } else
                return getAverageScore().apply(match);
        };
    }

    /**
     * This is the default scoring used to calculate the Document score by aggregating the child Element scores.
     * This combines the benefits of weights and exponential increase when calculating the average scores.
     *
     * @return the scoring function for ExponentialWeightedAverage
     */
    static ScoringFunction getExponentialWeightedAverageScore() {
        return match -> {
            List<Score> childScoreList = match.getChildScores();
            List<Score> perfectMatchedElements = getPerfectMatchedElement(childScoreList);

            // Apply Exponent if match elements > 1
            if (perfectMatchedElements.size() > 1 && getSumOfWeightedResult(perfectMatchedElements) > 1) {
                List<Score> notPerfectMachedElements = getNonPerfectMatchedElement(childScoreList);
                double numerator = getExponentiallyIncreasedValue(getSumOfWeightedResult(perfectMatchedElements))
                        + getSumOfWeightedResult(notPerfectMachedElements)
                        + getUnmatchedChildScore(match);

                double denominator = getExponentiallyIncreasedValue(getSumOfWeights(perfectMatchedElements))
                        + getSumOfWeights(notPerfectMachedElements)
                        + getChildCount(match)
                        - childScoreList.size();
                return new Score(numerator / denominator, match);
            } else
                return getWeightedAverageScore().apply(match);
        };
    }

    /**
     * This scoring assumes the child scores are binary (1.0 or 0.0).
     * Uses the jaccard method finding exact matches divided by total number of common children
     *
     * @return the scoring function for Jaccard
     */
    static ScoringFunction getJaccardScore() {
        return match ->
                new Score((double) match.getChildScores().size() /
                        ((match.getData().getChildCount(match.getMatchedWith()))), match);
    }

    static double getSumOfWeightedResult(List<Score> childScoreList) {
        return (childScoreList.stream().mapToDouble(d -> d.getResult() * d.getMatch().getWeight())).sum();
    }

    static double getSumOfResult(List<Score> childScoreList) {
        return (childScoreList.stream().mapToDouble(d -> d.getResult())).sum();
    }

    static double getSumOfWeights(List<Score> childScoreList) {
        return (childScoreList.stream().mapToDouble(d -> d.getMatch().getWeight())).sum();
    }

    static double getExponentiallyIncreasedValue(double value) {
        return Math.pow(value, EXPONENT);
    }

    static List<Score> getNonPerfectMatchedElement(List<Score> childScoreList) {
        return childScoreList.stream()
                .filter(d -> d.getResult() < EXPONENTIAL_INCREASE_THRESHOLD)
                .collect(Collectors.toList());
    }

    static List<Score> getPerfectMatchedElement(List<Score> childScoreList) {
        return childScoreList.stream()
                .filter(d -> d.getResult() >= EXPONENTIAL_INCREASE_THRESHOLD)
                .collect(Collectors.toList());
    }

    static double getChildCount(Match match) {
        return (double) match.getData().getChildCount(match.getMatchedWith());
    }

    static double getUnmatchedChildScore(Match match) {
        long maxUnmatchedChildCount = match.getData().getUnmatchedChildCount(match.getMatchedWith());
        return DEFAULT_UNMATCHED_CHILD_SCORE * maxUnmatchedChildCount;
    }
}
