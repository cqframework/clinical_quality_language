package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;

import java.util.ArrayList;
import java.util.List;

/*
PopulationVariance(argument List<Decimal>) Decimal
PopulationVariance(argument List<Quantity>) Quantity

The PopulationVariance operator returns the statistical population variance of the elements in source.
If the source contains no non-null elements, null is returned.
If the source is null, the result is null.
Return types: BigDecimal & Quantity
*/

public class PopulationVarianceEvaluator {

    public static Object popVariance(Object source, State state) {

        if (source == null) {
            return null;
        }

        if (source instanceof Iterable) {

            if (((List<?>) source).isEmpty()) {
                return null;
            }

            Object mean = AvgEvaluator.avg(source, state);

            List<Object> newVals = new ArrayList<>();

            ((List<?>) source).forEach(ae -> newVals.add(
                    MultiplyEvaluator.multiply(
                            SubtractEvaluator.subtract(ae, mean),
                            SubtractEvaluator.subtract(ae, mean))
                    )
            );

            return AvgEvaluator.avg(newVals, state);
        }

        throw new InvalidOperatorArgument(
                "PopulationVariance(List<Decimal>) or PopulationVariance(List<Quantity>)",
                String.format("PopulationVariance(%s)", source.getClass().getName())
        );
    }

}
