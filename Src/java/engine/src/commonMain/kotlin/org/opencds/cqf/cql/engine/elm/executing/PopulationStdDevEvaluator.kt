package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Quantity

/*
PopulationStdDev(argument List<Decimal>) Decimal
PopulationStdDev(argument List<Quantity>) Quantity

The PopulationStdDev operator returns the statistical standard deviation of the elements in source.
If the source contains no non-null elements, null is returned.
If the source is null, the result is null.
Return types: BigDecimal & Quantity
*/
object PopulationStdDevEvaluator {
    @JvmStatic
    fun popStdDev(source: Any?, state: State?): Any? {
        if (source == null) {
            return null
        }
        return when (
            val variance =
                PopulationVarianceEvaluator.popVariance(source, state, true, "PopulationStdDev")
        ) {
            // The cases in which PopulationVariance returns null are the same as those where
            // PopulationStdDev does.
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
