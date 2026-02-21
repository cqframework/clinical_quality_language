package org.opencds.cqf.cql.engine.execution.trace

import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.execution.State.ActivationFrame
import org.opencds.cqf.cql.engine.execution.Variable

/** Represents an expression def or function def that was evaluated. */
class ExpressionDefTraceFrame(
    /** The library containing the expression or function def. */
    library: VersionedIdentifier?,
    /** Expression or function def. */
    expressionDef: ExpressionDef,
    /** Variables in scope when the element was evaluated. */
    variables: List<Variable>,
    /** Context name and value. */
    context: Pair<String, Any?>,
    /** Result of evaluating the expression or function. Only used if tracing is enabled. */
    val result: Any?,
    /** Inner expression and function calls. */
    val subframes: List<TraceFrame>,
) : TraceFrame(library, expressionDef, variables, context) {
    override val element: ExpressionDef = expressionDef

    /** Arguments passed to the function. If the frame represents an expression, this is empty. */
    val arguments: List<Variable> =
        if (expressionDef is FunctionDef) {
            expressionDef.operand.map { operand -> variables.find { it.name == operand.name }!! }
        } else {
            emptyList()
        }

    /** Recursively stringifies the trace frame with indentation. */
    override fun toIndentedString(indentLevel: Int, showResults: Boolean): String {
        return buildString {
            append("  ".repeat(indentLevel))
            append("${library?.id ?: "?"}.${element.name}")
            if (element is FunctionDef) {
                append("(")
                append(arguments.joinToString(", ") { "${it.name} = ${it.value}" })
                append(")")
            }
            if (showResults) {
                append(" = $result")
            }
            appendLine()

            subframes.forEach { append(it.toIndentedString(indentLevel + 1, showResults)) }
        }
    }

    companion object {

        /** Recursively converts activation frames to trace frames. */
        fun fromActivationFrames(
            activationFrames: List<ActivationFrame>,
            contextValues: Map<String, Any?>,
        ): List<ExpressionDefTraceFrame> {
            return activationFrames.flatMap { activationFrame ->
                val subframes =
                    fromActivationFrames(activationFrame.innerActivationFrames, contextValues)

                val element = activationFrame.element

                // Only create trace frames for expressions and function calls
                if (element is ExpressionDef) {
                    return@flatMap listOf(
                        ExpressionDefTraceFrame(
                            activationFrame.library,
                            element,
                            activationFrame.variables.toList().reversed(),
                            activationFrame.contextName!! to
                                contextValues[activationFrame.contextName],
                            activationFrame.result,
                            subframes,
                        )
                    )
                }
                subframes
            }
        }
    }
}
