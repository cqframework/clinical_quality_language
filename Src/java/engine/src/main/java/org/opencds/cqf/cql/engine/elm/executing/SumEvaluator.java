package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;

/*
Sum(argument List<Integer>) Integer
Sum(argument List<Long>) Long
Sum(argument List<Decimal>) Decimal
Sum(argument List<Quantity>) Quantity

The Sum operator returns the sum of non-null elements in the source.
If the source contains no non-null elements, null is returned.
If the list is null, the result is null.
Return types: Integer, BigDecimal & Quantity
*/

public class SumEvaluator {

    public static Object sum(Object source, final State state) {
        if (source == null) {
            return null;
        }

        if (source instanceof Iterable<?> elements) {
            Object sum = null;
            for (Object element : elements) {
                if (element == null) {
                    continue;
                }

                if (sum == null) {
                    sum = element;
                } else {
                    sum = AddEvaluator.add(sum, element, state);
                }
            }

            return sum;
        }

        throw new InvalidOperatorArgument(
                "Sum(List<Integer>), Sum(List<Long>), Sum(List<Decimal>) or Sum(List<Quantity>)",
                String.format("Sum(%s)", source.getClass().getName()));
    }
}
