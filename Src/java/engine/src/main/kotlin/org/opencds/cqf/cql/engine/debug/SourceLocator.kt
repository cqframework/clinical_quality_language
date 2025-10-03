package org.opencds.cqf.cql.engine.debug

import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library

class SourceLocator(
    val librarySystemId: String?,
    val libraryName: String?,
    val libraryVersion: String?,
    val nodeId: String?,
    val nodeType: String?,
    val sourceLocation: Location?,
) {
    private val location: String
        get() =
            String.format(
                "%s%s",
                if (sourceLocation != null) sourceLocation.toLocator() else "?",
                if (nodeId != null || nodeType != null)
                    ("(" + (if (nodeId != null) nodeId else nodeType) + ")")
                else "(?)",
            )

    override fun toString(): String {
        val location = this.location
        return String.format(
            "%s%s",
            if (libraryName == null) "?" else libraryName,
            if (location != null) ("." + location) else "",
        )
    }

    companion object {
        @JvmStatic
        fun fromNode(node: Element, currentLibrary: Library?): SourceLocator {
            if (node is Element) {
                val element = node
                return SourceLocator(
                    if (currentLibrary != null) currentLibrary.identifier!!.system
                    else "http://cql.hl7.org/Library/unknown",
                    if (currentLibrary != null) currentLibrary.identifier!!.id else "?",
                    if (currentLibrary != null) currentLibrary.identifier!!.version else null,
                    element.localId,
                    stripEvaluator(element::class.simpleName),
                    if (element.locator != null) Location.fromLocator(element.locator!!) else null,
                )
            } else {
                return SourceLocator(
                    currentLibrary!!.identifier!!.system,
                    currentLibrary.identifier!!.id,
                    currentLibrary.identifier!!.version,
                    null,
                    stripEvaluator(node::class.simpleName),
                    null,
                )
            }
        }

        fun stripEvaluator(nodeType: String?): String? {
            if (nodeType == null) {
                return nodeType
            }

            if (nodeType.endsWith("Evaluator")) {
                return nodeType.substring(0, nodeType.lastIndexOf("Evaluator"))
            }

            return nodeType
        }
    }
}
