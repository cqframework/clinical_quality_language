package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*
exists(argument List<T>) Boolean

The exists operator returns true if the list contains any non-null elements.
If the argument is null, the result is null.
*/
object ExistsEvaluator {
    @JvmStatic
    fun exists(operand: CqlType?): Boolean {

        if (operand == null) {
            return Boolean.FALSE
        }

        if (operand is List) {
            return operand.any { it != null }.toCqlBoolean()
        }

        throw InvalidOperatorArgument("Exists(List<T>)", "Exists(${operand.typeAsString})")
    }
}
