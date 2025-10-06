package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlList
import org.opencds.cqf.cql.engine.runtime.Quantity

/*
Median(argument List<Decimal>) Decimal
Median(argument List<Quantity>) Quantity

The Median operator returns the median of the elements in source.
If the source contains no non-null elements, null is returned.
If the source is null, the result is null.
*/
object MedianEvaluator {
    @JvmStatic
    fun median(source: Any?, state: State?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            val element = source
            val itr = element.iterator()

            if (!itr.hasNext()) { // empty
                return null
            }

            val values = ArrayList<Any?>()
            while (itr.hasNext()) {
                val value = itr.next()
                if (value != null) {
                    values.add(value)
                }
            }

            if (values.isEmpty()) { // all null
                return null
            }

            values.sortWith(CqlList().valueSort)

            if (values.size % 2 != 0) {
                return values.get(values.size / 2)
            } else {
                if (values.get(0) is Int) { // size of list is even
                    return TruncatedDivideEvaluator.div(
                        AddEvaluator.add(
                            values.get(values.size / 2),
                            values.get((values.size / 2) - 1),
                        ),
                        2,
                        state,
                    )
                } else if (values.get(0) is BigDecimal || values.get(0) is Quantity) {
                    return DivideEvaluator.divide(
                        AddEvaluator.add(
                            values.get(values.size / 2),
                            values.get((values.size / 2) - 1),
                        ),
                        BigDecimal("2.0"),
                        state,
                    )
                }
            }
        }

        throw InvalidOperatorArgument(
            "Median(List<Decimal>) or Median(List<Quantity>)",
            String.format("Median(%s)", source.javaClass.name),
        )
    }
}
