package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.TemporalHelper;
import org.opencds.cqf.cql.engine.runtime.Time;

/*
simple type Time

The Time type represents time-of-day values within CQL.

CQL supports time values in the range @T00:00:00.0 to @T23:59:59.999 with a step size of 1 millisecond. Note that Time
    values may also optionally indicate an offset.

CQL also supports partial time values. For example, the time @T03 represents some instant during the hour of 3:00.
*/

public class TimeEvaluator extends org.cqframework.cql.elm.execution.Time {

    @Override
    protected Object internalEvaluate(Context context) {

        if (this.getHour() == null) {
            return null;
        }

        return new Time(
                TemporalHelper.cleanArray(
                        (Integer)this.getHour().evaluate(context),
                        this.getMinute() == null ? null : (Integer)this.getMinute().evaluate(context),
                        this.getSecond() == null ? null : (Integer)this.getSecond().evaluate(context),
                        this.getMillisecond() == null ? null : (Integer)this.getMillisecond().evaluate(context)
                )
        );
    }
}
