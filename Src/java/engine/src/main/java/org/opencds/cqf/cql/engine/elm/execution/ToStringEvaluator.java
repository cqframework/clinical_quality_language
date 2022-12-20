package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Ratio;
import org.opencds.cqf.cql.engine.runtime.Time;

/*

ToString(argument Boolean) String
ToString(argument Integer) String
ToString(argument Long) String
ToString(argument Decimal) String
ToString(argument Quantity) String
ToString(argument Ratio) String
ToString(argument Date) String
ToString(argument DateTime) String
ToString(argument Time) String

The ToString operator converts the value of its argument to a String value.
The operator uses the following string representations for each type:
Boolean	true|false
Integer	    (-)?#0
Long	    (-)?#0L
Decimal	    (-)?#0.0#
Quantity    (-)?#0.0# '<unit>'
Ratio       <quantity>:<quantity>
Date        YYYY-MM-DD
DateTime	YYYY-MM-DDThh:mm:ss.fff(+|-)hh:mm
Time	    Thh:mm:ss.fff(+|-)hh:mm
If the argument is null, the result is null.

*/

public class ToStringEvaluator extends org.cqframework.cql.elm.execution.ToString {

    public static Object toString(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof String) {
            return operand;
        }

        if (operand instanceof Integer) {
            return Integer.toString((Integer)operand);
        }
        else if (operand instanceof Long) {
            return operand.toString();
        }
        else if (operand instanceof BigDecimal) {
            return operand.toString();
        }
        else if (operand instanceof Quantity) {
            return operand.toString();
        }
        else if (operand instanceof Ratio) {
            return operand.toString();
        }
        else if (operand instanceof Boolean) {
            return Boolean.toString((Boolean)operand);
        }
        else if (operand instanceof Date) {
            return operand.toString();
        }
        else if (operand instanceof DateTime) {
            return operand.toString();
        }
        else if (operand instanceof Time) {
            return operand.toString();
        }
        // This is not standard - adding for test suite
        else {
            return operand.toString();
        }
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);

        return toString(operand);
    }
}
