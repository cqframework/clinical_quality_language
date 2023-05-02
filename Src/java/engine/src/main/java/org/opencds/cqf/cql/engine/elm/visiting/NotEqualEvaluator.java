package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.execution.State;

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

public class NotEqualEvaluator {

    public static Boolean notEqual(Object left, Object right, State state) {
        Boolean result = EqualEvaluator.equal(left, right, state);
        return result == null ? null : !result;
    }

}
