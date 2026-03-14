package org.opencds.cqf.cql.engine.debug

import io.github.oshai.kotlinlogging.KotlinLogging
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library

object DebugUtilities {
    private val logger = KotlinLogging.logger("DebugUtilities")

    fun logDebugResult(node: Element, currentLibrary: Library?, result: Any?) {
        val debugLocation = toDebugLocation(node)
        val debugString = toDebugString(result)
        logger.debug {
            "${if (currentLibrary != null) currentLibrary.identifier!!.id else "unknown"}.$debugLocation: $debugString"
        }
    }

    fun toDebugLocation(node: Element): String {
        var result = ""
        if (node.locator != null) {
            result = node.locator!!
        }
        if (node.localId != null) {
            result += "(" + node.localId + ")"
        }
        return result
    }

    @Suppress("ReturnCount")
    fun toDebugString(result: Any?): String {
        if (result is Iterable<*>) {
            val iterable = result
            return ("{" + iterable.joinToString(",") { item -> toDebugString(item) } + "}")
        }

        if (result != null) {
            return result.toString()
        }

        return "<null>"
    }
}
