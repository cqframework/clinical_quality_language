package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.Precision;

public class DateEvaluator {

    public static Object internalEvaluate(Integer year, Integer month, Integer day) {
        if (year == null) {
            return null;
        }
        Precision precision = Precision.YEAR;

        if (month == null) {
            month = 1;
        }
        else {
            precision = Precision.MONTH;
        }

        if (day == null) {
            day = 1;
        }
        else {
            precision = Precision.DAY;
        }

        return new Date(year, month, day).setPrecision(precision);
    }
}
