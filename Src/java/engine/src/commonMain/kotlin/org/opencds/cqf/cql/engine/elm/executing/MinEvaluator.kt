package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.util.javaClassName

/*
Min(argument List<Integer>) Integer
Min(argument List<Long>) Long
Min(argument List<Decimal>) Decimal
Min(argument List<Quantity>) Quantity
Min(argument List<Date>) Date
Min(argument List<DateTime>) DateTime
Min(argument List<Time>) Time
Min(argument List<String>) String

The Min operator returns the minimum element in the source. Comparison semantics are defined by the
    Comparison Operators for the type of value being aggregated.

If the source contains no non-null elements, null is returned.

If the source is null, the result is null.
*/
object MinEvaluator {
    @JvmStatic
    fun min(source: Any?, state: State?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            val element = source
            val itr = element.iterator()

            if (!itr.hasNext()) { // empty list
                return null
            }

            var min = itr.next()
            while (min == null && itr.hasNext()) {
                min = itr.next()
            }
            while (itr.hasNext()) {
                val value = itr.next()

                if (value == null) { // skip null
                    continue
                }

                val less = LessEvaluator.less(value, min, state)
                if (less != null && less) {
                    min = value
                }
            }
            return min
        }

        throw InvalidOperatorArgument(
            "Min(List<Integer>), Min(List<Long>), Min(List<Decimal>), Min(List<Quantity>), Min(List<Date>), Min(List<DateTime>), Min(List<Time>) or Min(List<String>)",
            "Min(${source.javaClassName})",
        )
    }
}
