package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;
import java.util.List;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
StdDev(argument List<Decimal>) Decimal
StdDev(argument List<Quantity>) Quantity

The StdDev operator returns the statistical standard deviation of the elements in source.
If the source contains no non-null elements, null is returned.
If the list is null, the result is null.
Return types: BigDecimal & Quantity
*/

public class StdDevEvaluator extends org.cqframework.cql.elm.execution.StdDev {

    public static Object stdDev(Object source, Context context) {

        if (source == null) {
            return null;
        }

        if (source instanceof Iterable) {

            if (((List<?>) source).isEmpty()) {
                return null;
            }

            Object variance = VarianceEvaluator.variance(source, context);

            return variance instanceof BigDecimal ?
                    PowerEvaluator.power(variance, new BigDecimal("0.5")) :
                    new Quantity().withValue((BigDecimal) PowerEvaluator.power(((Quantity) variance).getValue(),
                            new BigDecimal("0.5"))).withUnit(((Quantity) variance).getUnit());
        }

        throw new InvalidOperatorArgument(
                "StdDev(List<Decimal>) or StdDev(List<Quantity>)",
                String.format("StdDev(%s)", source.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);
        return stdDev(source, context);
    }
}
