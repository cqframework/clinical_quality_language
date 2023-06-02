package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.CqlList;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
Median(argument List<Decimal>) Decimal
Median(argument List<Quantity>) Quantity

The Median operator returns the median of the elements in source.
If the source contains no non-null elements, null is returned.
If the source is null, the result is null.
*/

public class MedianEvaluator extends org.cqframework.cql.elm.execution.Median {

    public static Object median(Object source, Context context) {
        if (source == null) {
            return null;
        }

        if (source instanceof Iterable) {
            Iterable<?> element = (Iterable<?>) source;
            Iterator<?> itr = element.iterator();

            if (!itr.hasNext()) { // empty
                return null;
            }

            ArrayList<Object> values = new ArrayList<>();
            while (itr.hasNext()) {
                Object value = itr.next();
                if (value != null) {
                    values.add(value);
                }
            }

            if (values.isEmpty()) { // all null
                return null;
            }

            values.sort(new CqlList().valueSort);

            if (values.size() % 2 != 0) {
                return values.get(values.size() / 2);
            } else {
                if (values.get(0) instanceof Integer) { // size of list is even
                    return TruncatedDivideEvaluator.div(
                            AddEvaluator.add(values.get(values.size() / 2), values.get((values.size() / 2) - 1)), 2, context
                    );
                } else if (values.get(0) instanceof BigDecimal || values.get(0) instanceof Quantity) {
                    return DivideEvaluator.divide(
                            AddEvaluator.add(values.get(values.size() / 2), values.get((values.size() / 2) - 1)), new BigDecimal("2.0"), context
                    );
                }
            }
        }

        throw new InvalidOperatorArgument(
                "Median(List<Decimal>) or Median(List<Quantity>)",
                String.format("Median(%s)", source.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);
        return median(source, context);
    }
}
