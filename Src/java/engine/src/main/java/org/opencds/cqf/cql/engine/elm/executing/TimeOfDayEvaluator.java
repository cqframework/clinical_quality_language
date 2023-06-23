package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.execution.State;

/*
TimeOfDay() Time

The TimeOfDay operator returns the time of day of the start timestamp associated with the evaluation request.
See the Now operator for more information on the rationale for defining the TimeOfDay operator in this way.
*/

public class TimeOfDayEvaluator {

    public static Object internalEvaluate(State state) {
        return TimeFromEvaluator.timeFrom(state.getEvaluationDateTime());
    }
}
