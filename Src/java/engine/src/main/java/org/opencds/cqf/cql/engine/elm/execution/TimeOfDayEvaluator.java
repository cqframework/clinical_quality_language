package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

/*
TimeOfDay() Time

The TimeOfDay operator returns the time of day of the start timestamp associated with the evaluation request.
See the Now operator for more information on the rationale for defining the TimeOfDay operator in this way.
*/

public class TimeOfDayEvaluator extends org.cqframework.cql.elm.execution.TimeOfDay {

    @Override
    protected Object internalEvaluate(Context context) {
        return TimeFromEvaluator.timeFrom(context.getEvaluationDateTime());
    }
}
