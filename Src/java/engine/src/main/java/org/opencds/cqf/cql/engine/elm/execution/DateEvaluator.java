package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.Precision;

public class DateEvaluator extends org.cqframework.cql.elm.execution.Date {

    @Override
    protected Object internalEvaluate(Context context) {
        Integer year = this.getYear() == null ? null : (Integer) this.getYear().evaluate(context);
        if (year == null) {
            return null;
        }
        Precision precision = Precision.YEAR;

        Integer month = this.getMonth() == null ? null : (Integer) this.getMonth().evaluate(context);
        if (month == null) {
            month = 1;
        }
        else {
            precision = Precision.MONTH;
        }

        Integer day = this.getDay() == null ? null : (Integer) this.getDay().evaluate(context);
        if (day == null) {
            day = 1;
        }
        else {
            precision = Precision.DAY;
        }

        return new Date(year, month, day).setPrecision(precision);
    }
}
