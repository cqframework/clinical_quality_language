package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.util.javaClassName

/*
Sum(argument List<Integer>) Integer
Sum(argument List<Long>) Long
Sum(argument List<Decimal>) Decimal
Sum(argument List<Quantity>) Quantity

The Sum operator returns the sum of non-null elements in the source.
If the source contains no non-null elements, null is returned.
If the list is null, the result is null.
Return types: Integer, BigDecimal & Quantity
*/
object SumEvaluator {
    @JvmStatic
    fun sum(source: Any?, state: State?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            var sum: Any? = null
            for (element in source) {
                if (element == null) {
                    continue
                }

                sum =
                    if (sum == null) {
                        element
                    } else {
                        AddEvaluator.add(sum, element, state)
                    }
            }

            return sum
        }

        throw InvalidOperatorArgument(
            "Sum(List<Integer>), Sum(List<Long>), Sum(List<Decimal>) or Sum(List<Quantity>)",
            "Sum(${source.javaClassName})",
        )
    }
}
