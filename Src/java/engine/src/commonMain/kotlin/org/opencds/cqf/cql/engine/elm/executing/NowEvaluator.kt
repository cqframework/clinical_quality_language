package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State

/*
Now() DateTime

The Now operator returns the date and time of the start timestamp associated with the evaluation request.
Now is defined in this way for two reasons:
1.	The operation will always return the same value within any given evaluation, ensuring that the result of
      an expression containing Now will always return the same result.
2.	The operation will return the timestamp associated with the evaluation request, allowing the evaluation to
      be performed with the same timezone information as the data delivered with the evaluation request.
*/
object NowEvaluator {
    @JvmStatic
    fun internalEvaluate(state: State?): Any? {
        return state!!.evaluationDateTime
    }
}
