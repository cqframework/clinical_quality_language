package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.runtime.Time

/*
simple type Time

The Time type represents time-of-day values within CQL.

CQL supports time values in the range @T00:00:00.0 to @T23:59:59.999 with a step size of 1 millisecond. Note that Time
    values may also optionally indicate an offset.

CQL also supports partial time values. For example, the time @T03 represents some instant during the hour of 3:00.
*/
object TimeEvaluator {
    @JvmStatic
    fun time(hour: Int?, minute: Int?, second: Int?, miliSecond: Int?): Any? {
        if (hour == null) {
            return null
        }

        return Time(*TemporalHelper.cleanArray(hour, minute, second, miliSecond))
    }
}
