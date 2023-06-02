package org.opencds.cqf.cql.engine.elm.execution;

import java.util.Iterator;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
Min(argument List<Integer>) Integer
Min(argument List<Long>) Long
Min(argument List<Decimal>) Decimal
Min(argument List<Quantity>) Quantity
Min(argument List<Date>) Date
Min(argument List<DateTime>) DateTime
Min(argument List<Time>) Time
Min(argument List<String>) String

The Min operator returns the minimum element in the source. Comparison semantics are defined by the
    Comparison Operators for the type of value being aggregated.

If the source contains no non-null elements, null is returned.

If the source is null, the result is null.
*/

public class MinEvaluator extends org.cqframework.cql.elm.execution.Min {

    public static Object min(Object source, Context context) {
        if (source == null) {
            return null;
        }

        if (source instanceof Iterable) {
            Iterable<?> element = (Iterable<?>)source;
            Iterator<?> itr = element.iterator();

            if (!itr.hasNext()) { // empty list
                return null;
            }

            Object min = itr.next();
            while (min == null && itr.hasNext()) {
                min = itr.next();
            }
            while (itr.hasNext()) {
                Object value = itr.next();

                if (value == null) { // skip null
                    continue;
                }

                Boolean less = LessEvaluator.less(value, min, context);
                if (less != null && less) {
                    min = value;
                }
            }
            return min;
        }

        throw new InvalidOperatorArgument(
            "Min(List<Integer>), Min(List<Long>), Min(List<Decimal>), Min(List<Quantity>), Min(List<Date>), Min(List<DateTime>), Min(List<Time>) or Min(List<String>)",
            String.format("Min(%s)", source.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);
        return min(source, context);
    }
}
