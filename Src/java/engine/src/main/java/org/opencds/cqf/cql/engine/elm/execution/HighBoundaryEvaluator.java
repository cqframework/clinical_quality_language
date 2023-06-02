package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Precision;
import org.opencds.cqf.cql.engine.runtime.Time;

/*

    HighBoundary(input Decimal, precision Integer) Decimal
    HighBoundary(input Date, precision Integer) Date
    HighBoundary(input DateTime, precision Integer) DateTime
    HighBoundary(input Time, precision Integer) Time

    The HighBoundary function returns the greatest possible value of the input to the specified precision.

    If no precision is specified, the greatest precision of the type of the input value is used (i.e. at least 8 for
        Decimal, 4 for Date, at least 17 for DateTime, and at least 9 for Time).

    If the precision is greater than the maximum possible precision of the implementation, the result is null.

    The function can be used with Decimal, Date, DateTime, and Time values.

    HighBoundary(1.587, 8) // 1.58799999
    HighBoundary(@2014, 6) // @2014-12
    HighBoundary(@2014-01-01T08, 17) // @2014-01-01T08:59:59.999
    HighBoundary(@T10:30, 9) // @T10:30:59.999

    If the input value is null, the result is null.

*/

public class HighBoundaryEvaluator extends org.cqframework.cql.elm.execution.HighBoundary {

    public static Object highBoundary(Object input, Object precision) {
        if (input == null) {
            return null;
        }

        if (input instanceof BigDecimal) {
            if (precision == null) {
                precision = 8;
            }

            if ((Integer) precision > 8) {
                return null;
            }

            BigDecimal result = new BigDecimal(((BigDecimal) input).toPlainString() + "99999999");
            return result.setScale((Integer) precision, RoundingMode.DOWN);
        }

        else if (input instanceof Date) {
            if (precision == null) {
                precision = 8;
            }

            if ((Integer) precision > 8) {
                return null;
            }

            if ((Integer) precision <= 4) {
                return ((Date) input).expandPartialMax(Precision.YEAR);
            }

            else if ((Integer) precision <= 6) {
                return ((Date) input).expandPartialMax(Precision.MONTH);
            }

            else if ((Integer) precision <= 8) {
                return ((Date) input).expandPartialMax(Precision.DAY);
            }
        }

        else if (input instanceof DateTime) {
            if (precision == null) {
                precision = 17;
            }

            if ((Integer) precision > 17) {
                return null;
            }

            if ((Integer) precision <= 4) {
                return ((DateTime) input).expandPartialMax(Precision.YEAR);
            }

            else if ((Integer) precision <= 6) {
                return ((DateTime) input).expandPartialMax(Precision.MONTH);
            }

            else if ((Integer) precision <= 8) {
                return ((DateTime) input).expandPartialMax(Precision.DAY);
            }

            else if ((Integer) precision <= 10) {
                return ((DateTime) input).expandPartialMax(Precision.HOUR);
            }

            else if ((Integer) precision <= 12) {
                return ((DateTime) input).expandPartialMax(Precision.MINUTE);
            }

            else if ((Integer) precision <= 14) {
                return ((DateTime) input).expandPartialMax(Precision.SECOND);
            }

            else if ((Integer) precision <= 17) {
                return ((DateTime) input).expandPartialMax(Precision.MILLISECOND);
            }
        }

        else if (input instanceof Time) {
            if (precision == null) {
                precision = 9;
            }

            if ((Integer) precision > 9) {
                return null;
            }

            if ((Integer) precision <= 2) {
                return ((Time) input).expandPartialMax(Precision.HOUR);
            }

            else if ((Integer) precision <= 4) {
                return ((Time) input).expandPartialMax(Precision.MINUTE);
            }

            else if ((Integer) precision <= 6) {
                return ((Time) input).expandPartialMax(Precision.SECOND);
            }

            else if ((Integer) precision <= 9) {
                return ((Time) input).expandPartialMax(Precision.MILLISECOND);
            }
        }

        throw new InvalidOperatorArgument(
                "HighBoundary(Decimal, Integer) or HighBoundary(Date, Integer) or HighBoundary(DateTime, Integer) or HighBoundary(Time, Integer)",
                String.format("HighBoundary(%s, %s)", input.getClass().getName(), precision.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object input = getOperand().get(0).evaluate(context);
        Object precision = getOperand().get(1).evaluate(context);
        return highBoundary(input, precision);
    }
}
