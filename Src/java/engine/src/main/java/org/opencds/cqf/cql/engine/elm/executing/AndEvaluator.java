package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
and (left Boolean, right Boolean) Boolean

The and operator returns true if both its arguments are true.
If either argument is false, the result is false. Otherwise, the result is null.

The following examples illustrate the behavior of the and operator:
define IsTrue = true and true
define IsFalse = true and false
define IsAlsoFalse = false and null
define IsNull = true and null
*/

public class AndEvaluator {

    public static Boolean and(Object left, Object right) {
        if (left == null && right == null) {
            return null;
        }

        if (left == null && right instanceof Boolean) {
            return (Boolean) right ? null : false;
        }

        if (right == null && left instanceof Boolean) {
            return (Boolean) left ? null : false;
        }

        if (left instanceof Boolean && right instanceof Boolean) {
            return (Boolean) left && (Boolean) right;
        }

        throw new InvalidOperatorArgument(
                "And(Boolean, Boolean)",
                String.format(
                        "And(%s, %s)",
                        left == null ? "Null" : left.getClass().getName(),
                        right == null ? "Null" : right.getClass().getName())
        );
    }
}
