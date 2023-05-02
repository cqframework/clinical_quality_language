package org.opencds.cqf.cql.engine.elm.visiting;


public class ReplaceMatchesEvaluator {

    public static Object replaceMatches(String argument, String pattern, String substitution) {
        if (argument == null || pattern == null || substitution == null) {
            return null;
        }

        return argument.replaceAll(pattern, substitution);
    }

}
