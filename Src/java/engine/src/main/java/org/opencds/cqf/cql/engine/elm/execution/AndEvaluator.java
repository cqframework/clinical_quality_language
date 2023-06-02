package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

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

public class AndEvaluator extends org.cqframework.cql.elm.execution.And {

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

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);

        return and(left, right);
    }
}
