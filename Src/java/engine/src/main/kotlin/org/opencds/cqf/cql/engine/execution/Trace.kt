package org.opencds.cqf.cql.engine.execution

import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.execution.State.ActivationFrame

/** Captures expression and function calls involved in evaluation with intermediate values. */
class Trace(val frames: List<Frame>) {

    /** Represents an expression or function call. */
    class Frame(
        /** The library containing the expression or function def. */
        val library: VersionedIdentifier?,
        /** Expression or function def. */
        val expressionDef: ExpressionDef,
        /**
         * Arguments passed to the function. If the frame represents an expression, this is empty.
         */
        val arguments: List<Variable>,
        /** Result of evaluating the expression or function. */
        val result: Any?,
        /** Inner expression and function calls. */
        val subframes: List<Frame>,
    ) {
        companion object {

            /** Recursively converts activation frames to trace frames. */
            fun fromActivationFrames(activationFrames: List<ActivationFrame>): List<Frame> {
                return activationFrames.flatMap { activationFrame ->
                    val subframes = fromActivationFrames(activationFrame.innerActivationFrames)

                    val element = activationFrame.element

                    // Only create trace frames for expressions and function calls
                    if (element is ExpressionDef) {
                        val arguments =
                            if (element is FunctionDef) {
                                element.operand.map { operand ->
                                    activationFrame.variables.find { it.name == operand.name }!!
                                }
                            } else {
                                emptyList()
                            }
                        return@flatMap listOf(
                            Frame(
                                activationFrame.library,
                                element,
                                arguments,
                                activationFrame.result,
                                subframes,
                            )
                        )
                    }
                    subframes
                }
            }
        }

        /** Recursively stringifies the trace frame with indentation. */
        fun toIndentedString(indentLevel: Int): String {
            return buildString {
                append("  ".repeat(indentLevel))
                append("${library?.id ?: "?"}.${expressionDef.name}")
                if (expressionDef is FunctionDef) {
                    append("(")
                    append(
                        arguments.joinToString(", ") {
                            "${it.name} = ${it.value?.toString() ?: "null"}"
                        }
                    )
                    append(")")
                }
                append(" = ${result?.toString() ?: "null"}")
                appendLine()

                subframes.forEach { append(it.toIndentedString(indentLevel + 1)) }
            }
        }
    }

    companion object {
        /** Creates a [Trace] from a list of activation frames. */
        fun fromActivationFrames(activationFrames: List<ActivationFrame>): Trace {
            return Trace(Frame.fromActivationFrames(activationFrames))
        }
    }

    override fun toString(): String {
        return buildString { frames.forEach { append(it.toIndentedString(0)) } }
    }
}
