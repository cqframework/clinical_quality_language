package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList

object ToListEvaluator {
    @JvmStatic
    fun toList(operand: Value?): List {
        // check to see if it is already a list
        if (operand is List) {
            return operand
        }

        return (if (operand == null) mutableListOf() else mutableListOf(operand)).toCqlList()
    }
}
