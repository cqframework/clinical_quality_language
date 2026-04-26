package org.opencds.cqf.cql.engine.execution.trace

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.execution.Variable
import org.opencds.cqf.cql.engine.runtime.Value

/**
 * Represents a sub-expression that was evaluated during detailed tracing. Captures the ELM node
 * type, source locator (if present), evaluation result, and nested subframes.
 */
class SubExpressionTraceFrame(
    library: VersionedIdentifier?,
    expression: Expression,
    variables: List<Variable>,
    context: Pair<String, Any?>,
    /** Result of evaluating the sub-expression. */
    val result: Value?,
    /** Nested trace frames (sub-expressions and/or expression def calls). */
    val subframes: List<TraceFrame>,
) : TraceFrame(library, expression, variables, context) {
    override val element: Expression = expression

    override fun toIndentedString(indentLevel: Int, showResults: Boolean): String {
        return buildString {
            append("  ".repeat(indentLevel))
            append(element::class.simpleName)
            val locator = element.locator
            if (locator != null) {
                append(" [$locator]")
            }
            if (showResults) {
                append(" = $result")
            }
            appendLine()

            subframes.forEach { append(it.toIndentedString(indentLevel + 1, showResults)) }
        }
    }
}
