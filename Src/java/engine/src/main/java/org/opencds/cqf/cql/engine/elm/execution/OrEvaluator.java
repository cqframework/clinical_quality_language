package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
or (left Boolean, right Boolean) Boolean

The or operator returns true if either of its arguments are true.
If both arguments are false, the result is false. Otherwise, the result is null.
*/

public class OrEvaluator extends org.cqframework.cql.elm.execution.Or {

    public static Boolean or(Object left, Object right) {
        if (left == null && right == null) {
            return null;
        }

        if (left == null && right instanceof Boolean) {
            return (Boolean) right ? true : null;
        }

        if (right == null && left instanceof Boolean) {
            return (Boolean) left ? true : null;
        }

        if (left instanceof Boolean && right instanceof Boolean) {
            return (Boolean) left || (Boolean) right;
        }

        throw new InvalidOperatorArgument(
                "Or(Boolean, Boolean)",
                String.format(
                        "Or(%s, %s)",
                        left == null ? "Null" : left.getClass().getName(),
                        right == null ? "Null" : right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        return or(left, right);
    }
}
