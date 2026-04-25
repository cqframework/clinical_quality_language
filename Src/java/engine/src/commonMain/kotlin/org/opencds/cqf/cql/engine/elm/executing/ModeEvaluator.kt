package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlList
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List

/*
Mode(argument List<T>) T

The Mode operator returns the statistical mode of the elements in source.
If the source contains no non-null elements, null is returned.
If the source is null, the result is null.
*/
object ModeEvaluator {
    @JvmStatic
    fun mode(source: CqlType?, state: State?): CqlType? {
        if (source == null) {
            return null
        }

        if (source is List) {
            val element = source
            val itr = element.iterator()

            if (!itr.hasNext()) { // empty list
                return null
            }

            val values = mutableListOf<CqlType?>()
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

            var max = 0
            var mode: CqlType? = null
            for (i in values.indices) {
                var count = 0
                for (j in i..<values.size) {
                    val equal = EqualEvaluator.equal(values.get(i), values.get(j), state)
                    if (equal != null && equal.value) {
                        ++count
                    }
                }
                if (count > max) {
                    mode = values.get(i)
                    max = count
                }
            }
            return mode
        }
        throw InvalidOperatorArgument("Mode(List<T>)", "Mode(${source.typeAsString})")
    }
}
