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

    LowBoundary(input Decimal, precision Integer) Decimal
    LowBoundary(input Date, precision Integer) Date
    LowBoundary(input DateTime, precision Integer) DateTime
    LowBoundary(input Time, precision Integer) Time

    The LowBoundary function returns the least possible value of the input to the specified precision.

    If no precision is specified, the greatest precision of the type of the input value is used (i.e. at least 8 for
        Decimal, 4 for Date, at least 17 for DateTime, and at least 9 for Time).

    If the precision is greater than the maximum possible precision of the implementation, the result is null.

    The function can be used with Decimal, Date, DateTime, and Time values.

    LowBoundary(1.587, 8) // 1.58700000
    LowBoundary(@2014, 6) // @2014-01
    LowBoundary(@2014-01-01T08, 17) // @2014-01-01T08:00:00.000
    LowBoundary(@T10:30, 9) // @T10:30:00.000

    If the input value is null, the result is null.

*/

public class LowBoundaryEvaluator extends org.cqframework.cql.elm.execution.LowBoundary {

    public static Object lowBoundary(Object input, Object precision) {
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

            return ((BigDecimal) input).setScale((Integer) precision, RoundingMode.DOWN);
        }

        else if (input instanceof Date) {
            if (precision == null) {
                precision = 8;
            }

            if ((Integer) precision > 8) {
                return null;
            }

            if ((Integer) precision <= 4) {
                return ((Date) input).setPrecision(Precision.YEAR);
            }

            else if ((Integer) precision <= 6) {
                return ((Date) input).setPrecision(Precision.MONTH);
            }

            else if ((Integer) precision <= 8) {
                return ((Date) input).setPrecision(Precision.DAY);
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
                return ((DateTime) input).setPrecision(Precision.YEAR);
            }

            else if ((Integer) precision <= 6) {
                return ((DateTime) input).setPrecision(Precision.MONTH);
            }

            else if ((Integer) precision <= 8) {
                return ((DateTime) input).setPrecision(Precision.DAY);
            }

            else if ((Integer) precision <= 10) {
                return ((DateTime) input).setPrecision(Precision.HOUR);
            }

            else if ((Integer) precision <= 12) {
                return ((DateTime) input).setPrecision(Precision.MINUTE);
            }

            else if ((Integer) precision <= 14) {
                return ((DateTime) input).setPrecision(Precision.SECOND);
            }

            else if ((Integer) precision <= 17) {
                return ((DateTime) input).setPrecision(Precision.MILLISECOND);
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
                return ((Time) input).setPrecision(Precision.HOUR);
            }

            else if ((Integer) precision <= 4) {
                return ((Time) input).setPrecision(Precision.MINUTE);
            }

            else if ((Integer) precision <= 6) {
                return ((Time) input).setPrecision(Precision.SECOND);
            }

            else if ((Integer) precision <= 9) {
                return ((Time) input).setPrecision(Precision.MILLISECOND);
            }
        }

        throw new InvalidOperatorArgument(
                "LowBoundary(Decimal, Integer) or LowBoundary(Date, Integer) or LowBoundary(DateTime, Integer) or LowBoundary(Time, Integer)",
                String.format("LowBoundary(%s, %s)", input.getClass().getName(), precision.getClass().getName())
        );
    }

     @Override
     protected Object internalEvaluate(Context context) {
         Object input = getOperand().get(0).evaluate(context);
         Object precision = getOperand().get(1).evaluate(context);
         return lowBoundary(input, precision);
     }
}
