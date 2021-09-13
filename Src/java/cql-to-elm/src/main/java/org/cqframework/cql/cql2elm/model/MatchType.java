package org.cqframework.cql.cql2elm.model;

public enum MatchType {
    EXACT, CASE_IGNORED, SOUNDS_LIKE, NONE;

    public static MatchType checkMatch(String val, String checkVal) {
        if (val.equals(checkVal)) {
            return EXACT;
        }

        if (val.equalsIgnoreCase(checkVal)) {
            return CASE_IGNORED;
        }

//        if (soundsLike(val, checkVal)) {
//            return SOUNDS_LIKE;
//        }

        return NONE;
    }

    //TODO: import org.apache.commons.codec.language.Soundex allows for "sounds like" matching:

//    private static boolean soundsLike(String val, String checkVal) {
//        return new Soundex().difference(val, checkVal) > 2;
//    }
}
