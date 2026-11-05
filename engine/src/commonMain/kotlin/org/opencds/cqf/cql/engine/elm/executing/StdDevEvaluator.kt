package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

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
    fun stdDev(source: Value?, state: State?): Value? {
        if (source == null) {
            return null
        }
        return when (val variance = VarianceEvaluator.variance(source, state, true, "StdDev")) {
            // The cases in which Variance returns null are the same as those where StdDev does.
            null -> return null
            is Decimal -> PowerEvaluator.power(variance, BigDecimal("0.5").toCqlDecimal())
            else -> {
                // If variance is a Quantity, we made sure that the unit part was not squared during
                // the variance computation. As a result, we can take the square root of the value
                // but keep the unit as it is.
                val value =
                    PowerEvaluator.power(
                        (variance as Quantity).value?.toCqlDecimal(),
                        BigDecimal("0.5").toCqlDecimal(),
                    ) as Decimal
                Quantity().withValue(value.value).withUnit(variance.unit)
            }
        }
    }
}
