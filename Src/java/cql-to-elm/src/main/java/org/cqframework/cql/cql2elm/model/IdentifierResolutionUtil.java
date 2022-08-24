package org.cqframework.cql.cql2elm.model;

import org.apache.commons.lang3.tuple.Pair;
import org.hl7.elm.r1.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IdentifierResolutionUtil {

    public static ResolvedIdentifier createMatch(String identifier, MatchType matchType, Object resolvedIdentifier) {
        return new ResolvedIdentifier(identifier, matchType, resolvedIdentifier);
    }

    public static ResolvedIdentifier getMatchType(String identifier, String val, String checkVal, Object resolvedIdentifier) {
        return new ResolvedIdentifier(identifier, MatchType.checkMatch(val, checkVal), resolvedIdentifier);
    }

    public static ResolvedIdentifier getFirstCaseMatch(List<ResolvedIdentifier> list) {
        for (ResolvedIdentifier match : list) {
            if (match.getMatchType().equals(MatchType.EXACT)) {
                return match;
            }
        }
        return null;
    }

    public static ResolvedIdentifier getFirstCaseMatchExpression(List<ResolvedIdentifier> list) {
        for (ResolvedIdentifier match : list) {
            if (match.getMatchType().equals(MatchType.EXACT) && canResolveToExpression(match.getResolvedElement())) {
                return match;
            }
        }
        return null;
    }

    public static List<ResolvedIdentifier> getAllMatches(List<ResolvedIdentifier> list, MatchType matchType) {
        return list.stream()
                .filter(s -> s.getMatchType().equals(matchType))
                .collect(Collectors.toList());
    }


    public static List<ResolvedIdentifier> miListFromPairList(List<Pair<String, Object>> input, MatchType matchType) {
        List<ResolvedIdentifier> ri = new ArrayList<>();
        if (input != null && input.size() > 0) {
            for (Pair<String, Object> pair : input) {
                ri.add(IdentifierResolutionUtil.createMatch(pair.getLeft(), matchType, pair.getRight()));
            }
        }
        return ri;
    }

    private static boolean canResolveToExpression(Object in) {
        try {
            Expression e = (Expression) in;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}