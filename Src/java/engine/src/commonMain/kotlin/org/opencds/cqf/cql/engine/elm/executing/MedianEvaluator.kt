package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlList
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.util.javaClassName

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
            val itr = source.iterator()

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

            values.sortWith(CqlList(state).valueSort)

            if (values.size % 2 != 0) {
                return values[values.size / 2]
            } else {
                if (values[0] is Int) { // size of list is even
                    return TruncatedDivideEvaluator.div(
                        AddEvaluator.add(
                            values[values.size / 2],
                            values[(values.size / 2) - 1],
                            state,
                        ),
                        2,
                        state,
                    )
                } else if (values[0] is BigDecimal || values[0] is Quantity) {
                    return DivideEvaluator.divide(
                        AddEvaluator.add(
                            values[values.size / 2],
                            values[(values.size / 2) - 1],
                            state,
                        ),
                        BigDecimal("2.0"),
                        state,
                    )
                }
            }
        }

        throw InvalidOperatorArgument(
            "Median(List<Decimal>) or Median(List<Quantity>)",
            "Median(${source.javaClassName})",
        )
    }
}
