package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State

/*
PopulationVariance(argument List<Decimal>) Decimal
PopulationVariance(argument List<Quantity>) Quantity

The PopulationVariance operator returns the statistical population variance of the elements in source.
If the source contains no non-null elements, null is returned.
If the source is null, the result is null.
Return types: BigDecimal & Quantity
*/
object PopulationVarianceEvaluator {
    @JvmStatic
    fun popVariance(source: Any?, state: State?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            if ((source as MutableList<*>).isEmpty()) {
                return null
            }

            val mean = AvgEvaluator.avg(source, state)

            val newVals = mutableListOf<Any?>()

            source.forEach { ae ->
                newVals.add(
                    MultiplyEvaluator.multiply(
                        SubtractEvaluator.subtract(ae, mean, state),
                        SubtractEvaluator.subtract(ae, mean, state),
                    )
                )
            }

            return AvgEvaluator.avg(newVals, state)
        }

        throw InvalidOperatorArgument(
            "PopulationVariance(List<Decimal>) or PopulationVariance(List<Quantity>)",
            "PopulationVariance(${source.javaClass.name})",
        )
    }
}
