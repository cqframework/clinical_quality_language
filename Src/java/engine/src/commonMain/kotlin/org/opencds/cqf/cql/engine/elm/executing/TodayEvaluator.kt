package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State

/*
Today() Date

The Today operator returns the date (with no time component) of the start timestamp associated with the evaluation request.
See the Now operator for more information on the rationale for defining the Today operator in this way.
*/
object TodayEvaluator {
    @JvmStatic
    fun today(state: State?): Any? {
        return DateFromEvaluator.dateFrom(state!!.evaluationDateTime)
    }
}
