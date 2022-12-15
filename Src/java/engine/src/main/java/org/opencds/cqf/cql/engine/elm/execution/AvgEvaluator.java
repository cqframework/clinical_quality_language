package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
Avg(argument List<Decimal>) Decimal
Avg(argument List<Quantity>) Quantity

* The Avg operator returns the average of the non-null elements in the source.
* If the source contains no non-null elements, null is returned.
* If the source is null, the result is null.
* Returns values of type BigDecimal or Quantity
*/

public class AvgEvaluator extends org.cqframework.cql.elm.execution.Avg {

    public static Object avg(Object source, Context context) {

        if (source == null) {
            return null;
        }

        if (source instanceof Iterable) {
            Iterable<?> elements = (Iterable<?>) source;
            Object avg = null;
            int size = 1;

            for (Object element : elements) {
                if (element == null) {
                    continue;
                }

                if (element instanceof BigDecimal || element instanceof Quantity) {
                    if (avg == null) {
                        avg = element;
                    } else {
                        ++size;
                        avg = AddEvaluator.add(avg, element);
                    }
                }
                else {
                    throw new InvalidOperatorArgument(
                            "Avg(List<Decimal>), Avg(List<Quantity>)",
                            String.format("Avg(List<%s>)", source.getClass().getName())
                    );
                }
            }

            return DivideEvaluator.divide(avg, new BigDecimal(size), context);
        }

        throw new InvalidOperatorArgument(
                "Avg(List<Decimal>), Avg(List<Quantity>)",
                String.format("Avg(%s)", source.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object src = getSource().evaluate(context);
        return avg(src, context);
    }
}
