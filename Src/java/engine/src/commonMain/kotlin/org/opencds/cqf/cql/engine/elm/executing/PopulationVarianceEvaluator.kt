package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Value

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
    fun popVariance(
        source: Value?,
        state: State?,
        stripSquareFromUnit: kotlin.Boolean = false,
        context: String = "PopulationVariance",
    ): Value? {
        val sumOfSquaredDifferences =
            VarianceEvaluator.sumOfSquaredDifferences(source, state, stripSquareFromUnit, context)
        return if (sumOfSquaredDifferences != null) AvgEvaluator.avg(sumOfSquaredDifferences, state)
        else null
    }
}
