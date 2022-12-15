package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.exception.TypeOverflow;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Precision;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.opencds.cqf.cql.engine.runtime.Value;

/*
successor of<T>(argument T) T

The successor operator returns the successor of the argument. For example, the successor of 1 is 2.
  If the argument is already the maximum value for the type, a run-time error is thrown.
The successor operator is defined for the Integer, Long, Decimal, DateTime, and Time types.
For Integer, Long successor is equivalent to adding 1.
For Decimal, successor is equivalent to adding the minimum precision value for the Decimal type, or 10^-08.
For DateTime and Time values, successor is equivalent to adding a time-unit quantity for the lowest specified precision of the value.
  For example, if the DateTime is fully specified, successor is equivalent to adding 1 millisecond;
    if the DateTime is specified to the second, successor is equivalent to adding one second, etc.
If the argument is null, the result is null.
*/

public class SuccessorEvaluator extends org.cqframework.cql.elm.execution.Successor {

    public static Object successor(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            if ((Integer) value >= Value.MAX_INT) {
                throw new TypeOverflow("The result of the successor operation exceeds the maximum value allowed for the Integer type");
            }
            return ((Integer)value) + 1;
        }
        else if (value instanceof Long) {
            if ((Long) value >= Value.MAX_LONG) {
                throw new TypeOverflow("The result of the successor operation exceeds the maximum value allowed for the Long type");
            }
            return ((Long)value) + 1;
        }
        else if (value instanceof BigDecimal) {
            if (((BigDecimal) value).compareTo(Value.MAX_DECIMAL) >= 0) {
                throw new TypeOverflow("The result of the successor operation exceeds the maximum value allowed for the Decimal type");
            }
            return ((BigDecimal)value).add(new BigDecimal("0.00000001"));
        }
        // NOTE: Quantity successor is not standard - including it for simplicity
        else if (value instanceof Quantity) {
            if (((Quantity) value).getValue().compareTo(Value.MAX_DECIMAL) >= 0) {
                throw new TypeOverflow("The result of the successor operation exceeds the maximum value allowed for the Decimal type");
            }
            Quantity quantity = (Quantity)value;
            return new Quantity().withValue((BigDecimal)successor(quantity.getValue())).withUnit(quantity.getUnit());
        }
        else if (value instanceof Date) {
            Date dt = (Date)value;
            return new Date(dt.getDate().plus(1, dt.getPrecision().toChronoUnit()), dt.getPrecision());
        }
        else if (value instanceof DateTime) {
            DateTime dt = (DateTime)value;
            return new DateTime(dt.getDateTime().plus(1, dt.getPrecision().toChronoUnit()), dt.getPrecision());
        }
        else if (value instanceof Time) {
            Time t = (Time)value;
            switch (t.getPrecision()) {
                case HOUR:
                    if (t.getTime().getHour() == 23) {
                        throw new TypeOverflow("The result of the successor operation exceeds the maximum value allowed for the Time type");
                    }
                    break;
                case MINUTE:
                    if (t.getTime().getHour() == 23 && t.getTime().getMinute() == 23) {
                        throw new TypeOverflow("The result of the successor operation exceeds the maximum value allowed for the Time type");
                    }
                    break;
                case SECOND:
                    if (t.getTime().getHour() == 23 && t.getTime().getMinute() == 23 && t.getTime().getSecond() == 59) {
                        throw new TypeOverflow("The result of the successor operation exceeds the maximum value allowed for the Time type");
                    }
                    break;
                case MILLISECOND:
                    if (t.getTime().getHour() == 23 && t.getTime().getMinute() == 59
                            && t.getTime().getSecond() == 59 && t.getTime().get(Precision.MILLISECOND.toChronoField()) == 999)
                    {
                        throw new TypeOverflow("The result of the successor operation exceeds the maximum value allowed for the Time type");
                    }
                    break;
                case DAY:
                case MONTH:
                case WEEK:
                case YEAR:
                    break;
            }
            return new Time(t.getTime().plus(1, t.getPrecision().toChronoUnit()), t.getPrecision());
        }

        throw new InvalidOperatorArgument(String.format("The Successor operation is not implemented for type %s", value.getClass().getName()));
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object value = this.getOperand().evaluate(context);
        return successor(value);
    }
}
