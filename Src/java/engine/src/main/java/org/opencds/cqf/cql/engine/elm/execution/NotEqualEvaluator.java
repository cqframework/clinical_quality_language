package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

/*
*** NOTES FOR INTERVAL ***
!=(left Interval<T>, right Interval<T>) Boolean

The not equal (!=) operator for intervals returns true if its arguments are not the same value.
The not equal operator is a shorthand for invocation of logical negation (not) of the equal operator.

*** NOTES FOR LIST ***
!=(left List<T>, right List<T>) Boolean

The not equal (!=) operator for lists returns true if its arguments are not the same value.
The not equal operator is a shorthand for invocation of logical negation (not) of the equal operator.
*/

public class NotEqualEvaluator extends org.cqframework.cql.elm.execution.NotEqual {

    public static Boolean notEqual(Object left, Object right, Context context) {
        Boolean result = EqualEvaluator.equal(left, right, context);
        return result == null ? null : !result;
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);

        return notEqual(left, right, context);
    }
}
