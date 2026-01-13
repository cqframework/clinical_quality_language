package org.opencds.cqf.cql.engine.execution.trace

import org.hl7.elm.r1.Element
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.execution.Variable

/** Represents an ELM element that was evaluated. */
open class TraceFrame(
    /** The library containing the element. */
    val library: VersionedIdentifier?,
    /** The ELM element represented by the frame. */
    open val element: Element,
    /** Variables in scope when the element was evaluated. */
    val variables: List<Variable>,
    /** Context name and value. */
    val context: Pair<String, Any?>,
) {
    /** Stringifies the trace frame with indentation. */
    open fun toIndentedString(indentLevel: Int, showResults: Boolean): String {
        return buildString {
            append("  ".repeat(indentLevel))
            append(element::class.simpleName)
            appendLine()
        }
    }
}
