package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Quantity

/*
Variance(argument List<Decimal>) Decimal
Variance(argument List<Quantity>) Quantity

The Variance operator returns the statistical variance of the elements in source.
If the source contains no non-null elements, null is returned.
If the source is null, the result is null.
Return types: BigDecimal & Quantity

stripSquareFromUnit is needed because we have no way of taking the square root of a unit. So the only way to obtain
ultimately correct units for computation that involve the square root of a quantity (mainly standard deviation-like
operations) is to avoid squared units in the intermediate results.
*/
object VarianceEvaluator {
    @JvmStatic
    fun sumOfSquaredDifferences(
        source: Any?,
        state: State?,
        stripSquareFromUnit: Boolean = false,
        context: String = "Variance",
    ): List<Any?>? {
        if (source == null) {
            return null
        }
        if (source !is Iterable<*>) {
            throw InvalidOperatorArgument(
                "$context(List<Decimal>) or $context(List<Quantity>)",
                "$context(${source.javaClass.name})",
            )
        }
        if (!source.iterator().hasNext()) {
            return null
        }

        val mean = AvgEvaluator.avg(source, state)
        val sumOfSquaredDifferences = mutableListOf<Any?>()
        for (element in source) {
            if (element == null) {
                // Skip the element
            } else if (!(element is BigDecimal || element is Quantity)) {
                throw InvalidOperatorArgument(
                    "$context(List<Decimal>) or $context(List<Quantity>)",
                    "$context(List<${element.javaClass.name}>)",
                )
            } else if (element is Quantity && stripSquareFromUnit) {
                val diff = SubtractEvaluator.subtract(element, mean, state) as Quantity
                // Multiply diff including its unit with the value of diff and unit 1 so that the
                // product has the unit
                // of diff instead of that unit squared.
                val squared =
                    MultiplyEvaluator.multiply(diff, Quantity().withValue(diff.value), state)
                sumOfSquaredDifferences.add(squared)
            } else {
                val diff = SubtractEvaluator.subtract(element, mean, state)
                val squared = MultiplyEvaluator.multiply(diff, diff, state)
                sumOfSquaredDifferences.add(squared)
            }
        }
        return sumOfSquaredDifferences
    }

    @JvmStatic
    fun variance(
        source: Any?,
        state: State?,
        stripSquareFromUnit: Boolean = false,
        context: String = "Variance",
    ): Any? {
        val sumOfSquaredDifferences =
            sumOfSquaredDifferences(source, state, stripSquareFromUnit, context)
        return if (sumOfSquaredDifferences != null)
            DivideEvaluator.divide(
                SumEvaluator.sum(sumOfSquaredDifferences, state),
                BigDecimal(sumOfSquaredDifferences.size - 1),
                state,
            ) // slight variation to Avg
        else null
    }
}
