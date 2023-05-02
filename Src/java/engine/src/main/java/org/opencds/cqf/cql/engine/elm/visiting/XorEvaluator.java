package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
xor (left Boolean, right Boolean) Boolean

The xor (exclusive or) operator returns true if one argument is true and the other is false.
If both arguments are true or both arguments are false, the result is false. Otherwise, the result is null.
*/

public class XorEvaluator {

    public static Object xor(Object left, Object right) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Boolean && right instanceof Boolean) {
            return ((Boolean) left ^ (Boolean) right);
        }

        throw new InvalidOperatorArgument(
                "Xor(Boolean, Boolean)",
                String.format("Xor(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

}
