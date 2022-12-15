package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

/*
timezone from(argument DateTime) Decimal

NOTE: This operator is _not_ part of CQL 1.4, it was renamed from 1.3 and is included so that the 1.4 engine can run 1.3 ELM
*/

public class TimezoneFromEvaluator extends org.cqframework.cql.elm.execution.TimezoneFrom {

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return TimezoneOffsetFromEvaluator.timezoneOffsetFrom(operand);
    }
}
