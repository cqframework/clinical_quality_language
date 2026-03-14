package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic

object ToListEvaluator {
    @JvmStatic
    fun toList(operand: Any?): Any? {
        // check to see if it is already a list
        if (operand is Iterable<*>) {
            return operand
        }

        return if (operand == null) mutableListOf<Any?>() else mutableListOf<Any?>(operand)
    }
}
