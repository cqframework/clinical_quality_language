package org.opencds.cqf.cql.engine.debug

import java.util.stream.Collectors
import java.util.stream.StreamSupport
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object DebugUtilities {
    private val logger: Logger = LoggerFactory.getLogger(DebugUtilities::class.java)

    fun logDebugResult(node: Element, currentLibrary: Library?, result: Any?) {
        val debugLocation = toDebugLocation(node)
        val debugString = toDebugString(result)
        logger.debug(
            "{}.{}: {}",
            if (currentLibrary != null) currentLibrary.identifier!!.id else "unknown",
            debugLocation,
            debugString,
        )
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

    fun toDebugString(result: Any?): String {
        if (result is Iterable<*>) {
            val iterable = result
            return ("{" +
                StreamSupport.stream(iterable.spliterator(), false)
                    .map { result -> toDebugString(result) }
                    .collect(Collectors.joining(",")) +
                "}")
        }

        if (result != null) {
            return result.toString()
        }

        return "<null>"
    }
}
