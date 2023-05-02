package org.opencds.cqf.cql.engine.elm.visiting;


public class MatchesEvaluator {

    public static Object matches(String argument, String pattern) {
        if (argument == null || pattern == null) {
            return null;
        }

        return argument.matches(pattern);
    }

}
