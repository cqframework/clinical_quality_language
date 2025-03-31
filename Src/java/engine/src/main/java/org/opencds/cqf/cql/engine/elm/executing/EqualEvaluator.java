package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.CqlList;
import org.opencds.cqf.cql.engine.runtime.CqlType;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
*** NOTES FOR CLINICAL OPERATORS ***
=(left Code, right Code) Boolean
=(left Concept, right Concept) Boolean

The equal (=) operator for Codes and Concepts uses tuple equality semantics.
  This means that the operator will return true if and only if the values for each element by name are equal.
If either argument is null, or contains any null components, the result is null.

*** NOTES FOR INTERVAL ***
=(left Interval<T>, right Interval<T>) Boolean

The equal (=) operator for intervals returns true if and only if the intervals are over the same point type,
  and they have the same value for the starting and ending points of the intervals as determined by the Start and End operators.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
=(left List<T>, right List<T>) Boolean

The equal (=) operator for lists returns true if and only if the lists have the same element type,
  and have the same elements by value, in the same order.
If either argument is null, or contains null elements, the result is null.

*/

public class EqualEvaluator {
    public static Boolean equal(Object left, Object right, State state) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Iterable && right instanceof Iterable) {
            return CqlList.equal((Iterable<?>) left, (Iterable<?>) right, state);
        }

        if (left instanceof Interval && right instanceof Integer) {
            return ((Interval) left).equal(right);
        }

        if (right instanceof Interval && left instanceof Integer) {
            return ((Interval) right).equal(left);
        }

        if (!left.getClass().equals(right.getClass())) {
            return false;
        } else if (left instanceof Boolean
                || left instanceof Integer
                || left instanceof Long
                || left instanceof String) {
            return left.equals(right);
        } else if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return ((BigDecimal) left).compareTo((BigDecimal) right) == 0;
        } else if (left instanceof CqlType && right instanceof CqlType) {
            return ((CqlType) left).equal(right);
        }

        if (state != null) {
            return state.getEnvironment().objectEqual(left, right);
        }

        throw new InvalidOperatorArgument(String.format(
                "Equal(%s, %s) requires Context and state was null",
                left.getClass().getName(), right.getClass().getName()));
    }

    public static Boolean equal(Object left, Object right) {
        return equal(left, right, null);
    }
}
