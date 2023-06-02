package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Precision;

/*
date from(argument DateTime) Date

NOTE: this is within the purview of DateTimeComponentFrom
  Description available in that class
*/

public class DateFromEvaluator extends org.cqframework.cql.elm.execution.DateFrom {

    public static Date dateFrom(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof DateTime) {
            if (((DateTime) operand).getPrecision().toDateTimeIndex() < 1) {
                return (Date) new Date(((DateTime) operand).getDateTime().getYear(), 1, 1).setPrecision(Precision.YEAR);
            }
            else if (((DateTime) operand).getPrecision().toDateTimeIndex() < 2) {
                return (Date) new Date(((DateTime) operand).getDateTime().getYear(), ((DateTime) operand).getDateTime().getMonthValue(), 1).setPrecision(Precision.MONTH);
            }
            else {
                return (Date) new Date(((DateTime) operand).getDateTime().getYear(), ((DateTime) operand).getDateTime().getMonthValue(), ((DateTime) operand).getDateTime().getDayOfMonth()).setPrecision(Precision.DAY);
            }
        }

        throw new InvalidOperatorArgument("date from(DateTime)", String.format("date from(%s)", operand.getClass().getName()));
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return dateFrom(operand);
    }
}
