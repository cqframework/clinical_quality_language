package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.elm.visiting.AddEvaluator;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Quantity;

import java.math.BigDecimal;

/*
Avg(argument List<Decimal>) Decimal
Avg(argument List<Quantity>) Quantity

* The Avg operator returns the average of the non-null elements in the source.
* If the source contains no non-null elements, null is returned.
* If the source is null, the result is null.
* Returns values of type BigDecimal or Quantity
*/

public class AvgEvaluator {

    public static Object avg(Object source, State state) {

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

            return DivideEvaluator.divide(avg, new BigDecimal(size), state);
        }

        throw new InvalidOperatorArgument(
                "Avg(List<Decimal>), Avg(List<Quantity>)",
                String.format("Avg(%s)", source.getClass().getName())
        );
    }
}
