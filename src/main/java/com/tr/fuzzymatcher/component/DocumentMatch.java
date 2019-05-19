package com.tr.fuzzymatcher.component;


import org.apache.commons.lang3.BooleanUtils;

import com.tr.fuzzymatcher.domain.Document;
import com.tr.fuzzymatcher.domain.Element;
import com.tr.fuzzymatcher.domain.Match;
import com.tr.fuzzymatcher.domain.Score;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <p>
 * Starts the Matching process by element level matching and aggregates the results back
 * This uses the ScoringFunction defined at each Document to get the aggregated Document score for matched Elements
 */
public class DocumentMatch {

    private static ElementMatch elementMatch = new ElementMatch();

    /**
     * Executes matching of a document stream
     *
     * @param documents Stream of Document objects
     * @return Stream of Match of Document type objects
     */
    public Stream<Match<Document>> matchDocuments(Stream<Document> documents) {
        Stream<Element> elements = documents.flatMap(d -> d.getPreProcessedElement().stream());
        Stream<Match<Element>> matchedElements = elementMatch.matchElements(elements);
        return rollupDocumentScore(matchedElements);
    }

    private Stream<Match<Document>> rollupDocumentScore(Stream<Match<Element>> matchElementStream) {

        Map<Document, Map<Document, List<Match<Element>>>> groupBy = matchElementStream
                .collect(Collectors.groupingBy(matchElement -> matchElement.getData().getDocument(),
                        Collectors.groupingBy(matchElement -> matchElement.getMatchedWith().getDocument())));

        return groupBy.entrySet().parallelStream().flatMap(leftDocumentEntry ->
                leftDocumentEntry.getValue().entrySet()
                        .parallelStream()
                        .flatMap(rightDocumentEntry -> {
                            List<Score> childScoreList = rightDocumentEntry.getValue()
                                    .stream()
                                    .map(d -> d.getScore())
                                    .collect(Collectors.toList());
                            Match<Document> leftMatch = new Match<Document>(leftDocumentEntry.getKey(), rightDocumentEntry.getKey(), childScoreList);
                            if (BooleanUtils.isNotFalse(rightDocumentEntry.getKey().isSource())) {
                                Match<Document> rightMatch = new Match<Document>(rightDocumentEntry.getKey(), leftDocumentEntry.getKey(), childScoreList);
                                return Stream.of(leftMatch, rightMatch);
                            }
                            return Stream.of(leftMatch);
                        }))
                .filter(match -> match.getResult() > match.getData().getThreshold());
    }
}
