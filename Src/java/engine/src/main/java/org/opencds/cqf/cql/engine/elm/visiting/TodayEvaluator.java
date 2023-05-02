package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.execution.State;

/*
Today() Date

The Today operator returns the date (with no time component) of the start timestamp associated with the evaluation request.
See the Now operator for more information on the rationale for defining the Today operator in this way.
*/

public class TodayEvaluator {

  public static Object today(State state) {
    return DateFromEvaluator.dateFrom(state.getEvaluationDateTime());
  }
}
