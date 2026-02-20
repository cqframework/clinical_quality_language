package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.CqlList

/*
exists(argument List<T>) Boolean

The exists operator returns true if the list contains any non-null elements.
If the argument is null, the result is null.
*/
object ExistsEvaluator {
    @JvmStatic
    fun exists(operand: Any?): Any? {
        val value = operand as Iterable<Any?>?

        if (value == null) {
            return false
        }

        return !CqlList.toList<Any?>(value, false).isEmpty()
    }
}
