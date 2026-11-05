package org.opencds.cqf.cql.engine.debug

import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpressionDef
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Value

interface BreakpointHandler {
    fun onBeforeExpression(elm: Element, state: State): BreakpointAction

    fun onAfterExpression(elm: Element, state: State, value: Value?) {}

    fun onExpressionDefEvaluated(elm: ExpressionDef, state: State, value: Value?) {}

    fun onExpressionDefEntered(elm: ExpressionDef, callSite: Element?, state: State) {}

    fun waitForResume() {}

    fun release() {}
}
