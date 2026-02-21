package org.opencds.cqf.cql.engine.exception

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.opencds.cqf.cql.engine.execution.State.ActivationFrame
import org.opencds.cqf.cql.engine.execution.trace.ExpressionDefTraceFrame
import org.opencds.cqf.cql.engine.execution.trace.TraceFrame

/**
 * A backtrace represents a series of trace frames from the root of an evaluation to the
 * sub-expression in which an exception was thrown.
 */
class Backtrace(
    /** The root of the trace frame series. */
    val frame: TraceFrame
) {
    override fun toString(): String {
        return frame.toIndentedString(0, false)
    }

    companion object {
        /**
         * Creates a [Backtrace] from a stack of activation frames and the expression in which the
         * exception was thrown.
         */
        fun fromActivationFrames(
            activationFrameStack: ArrayDeque<ActivationFrame>,
            expression: Expression,
            contextValues: Map<String, Any?>,
        ): Backtrace {
            val topActivationFrame = activationFrameStack.first()

            var frame =
                TraceFrame(
                    topActivationFrame.library,
                    expression,
                    topActivationFrame.variables.toList().reversed(),
                    topActivationFrame.contextName!! to
                        contextValues[topActivationFrame.contextName],
                )

            for (activationFrame in activationFrameStack) {
                val element = activationFrame.element
                if (element is ExpressionDef) {
                    frame =
                        ExpressionDefTraceFrame(
                            activationFrame.library,
                            element,
                            activationFrame.variables.toList().reversed(),
                            activationFrame.contextName!! to
                                contextValues[activationFrame.contextName],
                            activationFrame.result,
                            listOf(frame),
                        )
                }
            }

            return Backtrace(frame)
        }
    }
}
