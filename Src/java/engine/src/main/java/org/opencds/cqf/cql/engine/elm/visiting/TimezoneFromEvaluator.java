package org.opencds.cqf.cql.engine.elm.visiting;

/*
timezone from(argument DateTime) Decimal

NOTE: This operator is _not_ part of CQL 1.4, it was renamed from 1.3 and is included so that the 1.4 engine can run 1.3 ELM
*/

public class TimezoneFromEvaluator {

    public static Object internalEvaluate(Object operand) {
        return TimezoneOffsetFromEvaluator.timezoneOffsetFrom(operand);
    }
}
