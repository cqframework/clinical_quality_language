package org.opencds.cqf.cql.engine.elm.executing;

/*
is null(argument Any) Boolean

The is null operator determines whether or not its argument evaluates to null.
If the argument evaluates to null, the result is true; otherwise, the result is false.
*/

public class IsNullEvaluator {

    public static Object isNull(Object operand) {
        return operand == null;
    }
}
