package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.util.javaClassName

/*
Max(argument List<Integer>) Integer
Max(argument List<Long>) Long
Max(argument List<Decimal>) Decimal
Max(argument List<Quantity>) Quantity
Max(argument List<Date>) Date
Max(argument List<DateTime>) DateTime
Max(argument List<Time>) Time
Max(argument List<String>) String

The Max operator returns the maximum element in the source. Comparison semantics are defined by the
    Comparison Operators for the type of value being aggregated.

If the source contains no non-null elements, null is returned.

If the source is null, the result is null.
*/
object MaxEvaluator {
    @JvmStatic
    fun max(source: Any?, state: State?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            val element = source
            val itr = element.iterator()

            if (!itr.hasNext()) { // empty list
                return null
            }

            var max = itr.next()
            while (max == null && itr.hasNext()) {
                max = itr.next()
            }

            while (itr.hasNext()) {
                val value = itr.next()

                if (value == null) { // skip null
                    continue
                }

                val greater = GreaterEvaluator.greater(value, max, state)
                if (greater != null && greater) {
                    max = value
                }
            }
            return max
        }

        throw InvalidOperatorArgument(
            "Max(List<Integer>), Max(List<Long>, Max(List<Decimal>, Max(List<Quantity>), Max(List<Date>), Max(List<DateTime>), Max(List<Time>) or Max(List<String>))",
            "Max(${source.javaClassName})",
        )
    }
}
