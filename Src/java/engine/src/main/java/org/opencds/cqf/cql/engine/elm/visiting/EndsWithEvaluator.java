package org.opencds.cqf.cql.engine.elm.visiting;


public class EndsWithEvaluator {

    public static Object endsWith(String argument, String suffix) {
        if (argument == null || suffix == null) {
            return null;
        }
        return argument.endsWith(suffix);
    }

}
