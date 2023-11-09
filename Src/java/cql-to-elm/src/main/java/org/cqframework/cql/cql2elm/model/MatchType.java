package org.cqframework.cql.cql2elm.model;

public enum MatchType {
    EXACT, CASE_IGNORED, NONE;

    public static MatchType resolveMatchType(String val, String checkVal) {
        if (val.equals(checkVal)) {
            return EXACT;
        }

        if (val.equalsIgnoreCase(checkVal)) {
            return CASE_IGNORED;
        }

        return NONE;
    }
}