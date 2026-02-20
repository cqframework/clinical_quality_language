package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
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
        return when (val variance = VarianceEvaluator.variance(source, state, true, "StdDev")) {
            // The cases in which Variance returns null are the same as those where StdDev does.
            null -> return null
            is BigDecimal -> PowerEvaluator.power(variance, BigDecimal("0.5"))
            else -> {
                // If variance is a Quantity, we made sure that the unit part was not squared during
                // the variance computation. As a result, we can take the square root of the value
                // but keep the unit as it is.
                val value =
                    PowerEvaluator.power((variance as Quantity).value, BigDecimal("0.5"))
                        as BigDecimal
                Quantity().withValue(value).withUnit(variance.unit)
            }
        }
    }
}
