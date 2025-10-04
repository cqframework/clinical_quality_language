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
            "${if (sourceLocation != null) sourceLocation.toLocator() else "?"}${
                if (nodeId != null || nodeType != null)
                    ("(" + (if (nodeId != null) nodeId else nodeType) + ")")
                else "(?)"
            }"

    override fun toString(): String {
        val location = this.location
        return "${libraryName ?: "?"}${".$location"}"
    }

    companion object {
        @JvmStatic
        fun fromNode(node: Element, currentLibrary: Library?): SourceLocator {
            return SourceLocator(
                if (currentLibrary != null) currentLibrary.identifier!!.system
                else "http://cql.hl7.org/Library/unknown",
                if (currentLibrary != null) currentLibrary.identifier!!.id else "?",
                if (currentLibrary != null) currentLibrary.identifier!!.version else null,
                node.localId,
                stripEvaluator(node::class.simpleName),
                if (node.locator != null) Location.fromLocator(node.locator!!) else null,
            )
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
