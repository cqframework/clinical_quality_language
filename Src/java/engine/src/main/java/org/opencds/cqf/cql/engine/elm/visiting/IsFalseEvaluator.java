package org.opencds.cqf.cql.engine.elm.visiting;

/*
is false(argument Boolean) Boolean

The is false operator determines whether or not its argument evaluates to false.
If the argument evaluates to false, the result is true; otherwise, the result is false.
*/

public class IsFalseEvaluator {
    public static Object isFalse(Boolean operand) {
        return Boolean.FALSE == operand;
    }

}
