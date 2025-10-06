package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Quantity

/*
StdDev(argument List<Decimal>) Decimal
StdDev(argument List<Quantity>) Quantity

The StdDev operator returns the statistical standard deviation of the elements in source.
If the source contains no non-null elements, null is returned.
If the list is null, the result is null.
Return types: BigDecimal & Quantity
*/
object StdDevEvaluator {
    @JvmStatic
    fun stdDev(source: Any?, state: State?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            if ((source as MutableList<*>).isEmpty()) {
                return null
            }

            val variance = VarianceEvaluator.variance(source, state)
            // The cases in which Variance returns null are the same as those where StdDev does.
            if (variance == null) {
                return null
            }

            return if (variance is BigDecimal) PowerEvaluator.power(variance, BigDecimal("0.5"))
            else
                Quantity()
                    .withValue(
                        PowerEvaluator.power((variance as Quantity).value, BigDecimal("0.5"))
                            as BigDecimal
                    )
                    .withUnit(variance.unit)
        }

        throw InvalidOperatorArgument(
            "StdDev(List<Decimal>) or StdDev(List<Quantity>)",
            String.format("StdDev(%s)", source.javaClass.name),
        )
    }
}
