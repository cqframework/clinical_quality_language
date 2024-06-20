package org.opencds.cqf.cql.engine.elm.executing;

import java.util.List;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
    There are two overloads of this operator:
        List, T: The type of T must be the same as the element type of the list.
        Interval, T : The type of T must be the same as the point type of the interval.

    For the List, T overload, this operator returns true if the given element is in the list,
        and it is not the only element in the list, using equivalence semantics.
    For the Interval, T overload, this operator returns true if the given point is greater than
        the starting point of the interval, and less than the ending point of the interval, as
        determined by the Start and End operators.
        If precision is specified and the point type is a date/time type, comparisons used in the
            operation are performed at the specified precision.
*/

public class ProperContainsEvaluator {

    public static Boolean properContains(Object left, Object right, State state) {
        if (left instanceof Interval) {
            Boolean startProperContains = GreaterEvaluator.greater(right, ((Interval) left).getStart(), state);
            Boolean endProperContains = LessEvaluator.less(right, ((Interval) left).getEnd(), state);

            return startProperContains == null
                    ? null
                    : endProperContains == null ? null : startProperContains && endProperContains;
        } else if (left instanceof Iterable) {
            List<?> leftList = (List<?>) left;

            for (Object element : leftList) {
                Boolean isElementInList = EquivalentEvaluator.equivalent(element, right, state);
                if (isElementInList == null) {
                    return null;
                }

                if (isElementInList && leftList.size() > 1) {
                    return true;
                }
            }

            return false;
        }

        throw new InvalidOperatorArgument(
                "ProperContains(List<T>, T) or ProperContains(Interval<T>, T)",
                String.format(
                        "ProperContains(%s, %s)",
                        left.getClass().getName(), right.getClass().getName()));
    }

    public static Boolean properContains(Object left, Object right, String precision, State state) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof BaseTemporal) {
            Boolean startProperContains = AfterEvaluator.after(right, ((Interval) left).getStart(), precision, state);
            Boolean endProperContains = BeforeEvaluator.before(right, ((Interval) left).getEnd(), precision, state);

            return startProperContains == null
                    ? null
                    : endProperContains == null ? null : startProperContains && endProperContains;
        }

        return properContains(left, right, state);
    }
}
