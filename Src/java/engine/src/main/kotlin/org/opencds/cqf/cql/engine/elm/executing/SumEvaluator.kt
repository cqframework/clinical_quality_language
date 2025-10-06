package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

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
    fun sum(source: Any?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            val elements = source
            var sum: Any? = null
            for (element in elements) {
                if (element == null) {
                    continue
                }

                if (sum == null) {
                    sum = element
                } else {
                    sum = AddEvaluator.add(sum, element)
                }
            }

            return sum
        }

        throw InvalidOperatorArgument(
            "Sum(List<Integer>), Sum(List<Long>), Sum(List<Decimal>) or Sum(List<Quantity>)",
            "Sum(${source.javaClass.name})",
        )
    }
}
