package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Time;

/*

    Precision(argument Decimal) Integer
    Precision(argument Date) Integer
    Precision(argument DateTime) Integer
    Precision(argument Time) Integer

    The Precision function returns the number of digits of precision in the input value.

    The function can be used with Decimal, Date, DateTime, and Time values.

    For Decimal values, the function returns the number of digits of precision after the decimal place in the input value.
    Precision(1.58700) // 5

    For Date and DateTime values, the function returns the number of digits of precision in the input value.
    Precision(@2014) // 4
    Precision(@2014-01-05T10:30:00.000) // 17
    Precision(@T10:30) // 4
    Precision(@T10:30:00.000) // 9
    If the argument is null, the result is null.

*/

public class PrecisionEvaluator extends org.cqframework.cql.elm.execution.Precision {

    public static Integer precision(Object argument) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof BigDecimal) {
            String string = ((BigDecimal) argument).toPlainString();
            int index = string.indexOf(".");
            return index < 0 ? 0 : string.length() - index - 1;
        }

        else if (argument instanceof Date) {
            return argument.toString().replaceAll("-", "").length();
        }

        else if (argument instanceof DateTime) {
            return argument.toString().replaceAll("[T.:-]", "").length();
        }

        else if (argument instanceof Time) {
            return argument.toString().replaceAll("[T.:]", "").length();
        }

        throw new InvalidOperatorArgument(
                "Precision(Decimal), Precision(Date), Precision(DateTime) or Precision(Time)",
                String.format("Precision(%s)", argument.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object argument = getOperand().evaluate(context);
        return precision(argument);
    }
}
