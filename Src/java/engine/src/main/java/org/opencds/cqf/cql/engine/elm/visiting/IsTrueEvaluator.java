package org.opencds.cqf.cql.engine.elm.visiting;

/*
is true(argument Boolean) Boolean

The is true operator determines whether or not its argument evaluates to true.
If the argument evaluates to true, the result is true; otherwise, the result is false.
*/

public class IsTrueEvaluator {
    public static Object isTrue(Boolean operand) {
        return Boolean.TRUE == operand;
    }

}
