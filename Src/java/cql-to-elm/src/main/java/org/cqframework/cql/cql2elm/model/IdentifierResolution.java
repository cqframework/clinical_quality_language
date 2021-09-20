package org.cqframework.cql.cql2elm.model;

import org.apache.commons.lang3.tuple.Pair;
import org.hl7.elm.r1.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IdentifierResolution {

    private MatchType matchType;
    private Object resolvedElement;
    private String identifier;

    private IdentifierResolution() {
        //class should only be initialized via checkMatch
    }

    private IdentifierResolution(String identifier,
                                 MatchType matchType,
                                 Object resolvedElement) {

        this.matchType = matchType;
        this.identifier = identifier;
        this.resolvedElement = resolvedElement;

    }
    public static IdentifierResolution createMatch(String identifier, MatchType matchType, Object resolvedIdentifier) {
        return new IdentifierResolution(identifier, matchType, resolvedIdentifier);
    }

    public static IdentifierResolution getMatchType(String identifier, String val, String checkVal, Object resolvedIdentifier) {
        return new IdentifierResolution(identifier, MatchType.checkMatch(val, checkVal), resolvedIdentifier);
    }

    public MatchType getMatchType() {
        return this.matchType;
    }

    public Object getResolvedElement() {
        return resolvedElement;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static IdentifierResolution getFirstCaseMatch(List<IdentifierResolution> list){
        for (IdentifierResolution match : list){
            if (match.getMatchType().equals(MatchType.EXACT)){
                return match;
            }
        }
        return null;
    }

    public static IdentifierResolution getFirstCaseMatchExpression(List<IdentifierResolution> list){
        for (IdentifierResolution match : list){
            if (match.getMatchType().equals(MatchType.EXACT) && canResolveToExpression(match.getResolvedElement())){
                return match;
            }
        }
        return null;
    }

    public static List<IdentifierResolution> getAllMatches(List<IdentifierResolution> list, MatchType matchType){
        return list.stream()
                .filter(s -> s.getMatchType().equals(matchType) )
                .collect(Collectors.toList());
    }


    public static List<IdentifierResolution> miListFromPairList(List<Pair<String, Object>> input, MatchType matchType){
        List<IdentifierResolution> ri = new ArrayList<>();
        if (input != null && input.size() > 0){
            for (Pair<String, Object> pair : input){
                ri.add(IdentifierResolution.createMatch(pair.getLeft(), matchType, pair.getRight()));
            }
        }
        return ri;
    }

    private static boolean canResolveToExpression(Object in) {
        try {
            Expression e = (Expression) in;
            return true;
        }catch (Exception e){
            return false;
        }
    }
}