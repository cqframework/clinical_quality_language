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
*/
object VarianceEvaluator {
    @JvmStatic
    fun variance(source: Any?, state: State?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            if ((source as List<*>).isEmpty()) {
                return null
            }

            val mean = AvgEvaluator.avg(source, state)

            val newVals = mutableListOf<Any?>()

            for (element in source) {
                if (element != null) {
                    if (element is BigDecimal || element is Quantity) {
                        newVals.add(
                            MultiplyEvaluator.multiply(
                                SubtractEvaluator.subtract(element, mean),
                                SubtractEvaluator.subtract(element, mean),
                            )
                        )
                    } else {
                        throw InvalidOperatorArgument(
                            "Variance(List<Decimal>) or Variance(List<Quantity>)",
                            String.format("Variance(List<%s>)", element.javaClass.name),
                        )
                    }
                }
            }

            return DivideEvaluator.divide(
                SumEvaluator.sum(newVals),
                BigDecimal(newVals.size - 1),
                state,
            ) // slight variation to Avg
        }

        throw InvalidOperatorArgument(
            "Variance(List<Decimal>) or Variance(List<Quantity>)",
            String.format("Variance(%s)", source.javaClass.name),
        )
    }
}
