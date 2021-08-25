package org.cqframework.cql.cql2elm.model;

import org.apache.commons.lang3.tuple.Pair;
import org.cqframework.cql.cql2elm.ConditionalIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchIdentifier {

    private Match matchType;
    private Object resolvedIdentifier;
    private String identifier;

    private MatchIdentifier() {
        //class should only be initialized via checkMatch
    }

    private MatchIdentifier(String identifier,
            Match matchType,
            Object resolvedIdentifier) {

        this.matchType = matchType;
        this.identifier = identifier;
        this.resolvedIdentifier = resolvedIdentifier;

    }
    public static MatchIdentifier createMatch(String identifier, Match matchType, Object resolvedIdentifier) {
        return new MatchIdentifier(identifier, matchType, resolvedIdentifier);
    }

    public static MatchIdentifier checkMatch(String identifier, String val, String checkVal, Object resolvedIdentifier) {
        return new MatchIdentifier(identifier, Match.checkMatch(val, checkVal), resolvedIdentifier);
    }

    /**
     * This method is used to set a different identifier given a match condition
     * @param val
     * @param checkVal
     * @param conditionalIdentifier
     * @return
     */
    public static MatchIdentifier checkMatch(String identifier, String val, String checkVal, ConditionalIdentifier conditionalIdentifier) {
        Match matchType = Match.checkMatch(val, checkVal);
        Object resolvedIdentifier = conditionalIdentifier.getConditionalIdentifier(matchType);
        return new MatchIdentifier(identifier, matchType, resolvedIdentifier);
    }

    public Match getMatchType() {
        return this.matchType;
    }

    public Object getResolvedIdentifier() {
        return resolvedIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static MatchIdentifier getFirstCaseMatch(List<MatchIdentifier> list){
        for (MatchIdentifier match : list){
            if (match.getMatchType().equals(Match.CASE)){
                return match;
            }
        }
        return null;
    }

    public static List<MatchIdentifier> getAllMatches(List<MatchIdentifier> list, Match matchType){
        return list.stream()
                .filter(s -> s.getMatchType().equals(matchType) )
                .collect(Collectors.toList());
    }


    public static List<MatchIdentifier> miListFromPairList(List<Pair<String, Object>> input, Match matchType){
        List<MatchIdentifier> ri = new ArrayList<>();
        if (input != null && input.size() > 0){
            for (Pair<String, Object> pair : input){
                ri.add(MatchIdentifier.createMatch(pair.getLeft(), matchType, pair.getRight()));
            }
        }
        return ri;
    }
}


