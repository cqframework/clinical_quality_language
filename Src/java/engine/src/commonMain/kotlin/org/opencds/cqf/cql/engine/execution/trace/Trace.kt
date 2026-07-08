package org.opencds.cqf.cql.engine.execution.trace

import org.opencds.cqf.cql.engine.execution.State

/** Captures expression and function calls involved in evaluation with intermediate values. */
class Trace(val frames: List<TraceFrame>) {

    override fun toString(): String {
        return buildString { frames.forEach { append(it.toIndentedString(0, true)) } }
    }

    companion object {
        /** Creates a [Trace] from a list of activation frames. */
        fun fromActivationFrames(
            activationFrames: List<State.ActivationFrame>,
            contextValues: Map<String, String?>,
            detailed: Boolean = false,
        ): Trace {
            return Trace(
                ExpressionDefTraceFrame.fromActivationFrames(
                    activationFrames,
                    contextValues,
                    detailed,
                )
            )
        }
    }
}
