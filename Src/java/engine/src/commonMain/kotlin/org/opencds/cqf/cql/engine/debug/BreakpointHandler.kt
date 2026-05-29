package org.opencds.cqf.cql.engine.debug

import org.hl7.elm.r1.Element
import org.opencds.cqf.cql.engine.execution.State

interface BreakpointHandler {
    fun onBeforeExpression(elm: Element, state: State): BreakpointAction
    fun onAfterExpression(elm: Element, state: State, value: Any?) {}
    fun waitForResume() {}
    fun release() {}
}
