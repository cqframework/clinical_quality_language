package org.opencds.cqf.cql.engine.debug

import kotlin.jvm.JvmStatic
import kotlinx.serialization.Serializable
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library

@Serializable
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
            "${sourceLocation?.toLocator() ?: "?"}${if (nodeId != null || nodeType != null)
    ("(" + (nodeId ?: nodeType) + ")")
else "(?)"}"

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

        @Suppress("ReturnCount")
        fun stripEvaluator(nodeType: String?): String? {
            if (nodeType == null) {
                return nodeType
            }

            if (nodeType.endsWith("Evaluator")) {
                return nodeType.dropLast("Evaluator".length)
            }

            return nodeType
        }
    }
}
