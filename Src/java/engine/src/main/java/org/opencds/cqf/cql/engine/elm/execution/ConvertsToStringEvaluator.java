package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Ratio;
import org.opencds.cqf.cql.engine.runtime.Time;

/*

    ConvertsToString(argument Boolean) Boolean
    ConvertsToString(argument Integer) Boolean
    ConvertsToString(argument Long) Boolean
    ConvertsToString(argument Decimal) Boolean
    ConvertsToString(argument Quantity) Boolean
    ConvertsToString(argument Ratio) Boolean
    ConvertsToString(argument Date) Boolean
    ConvertsToString(argument DateTime) Boolean
    ConvertsToString(argument Time) Boolean
    Description:

    The ConvertsToString operator returns true if its argument is or can be converted to a String value. See the ToString operator
        for a description of the supported conversions.

    If the argument is null, the result is null.

*/

public class ConvertsToStringEvaluator extends org.cqframework.cql.elm.execution.ConvertsToString {

    public static Boolean convertsToString(Object argument) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof Boolean || argument instanceof Integer || argument instanceof Long || argument instanceof BigDecimal
            || argument instanceof Quantity || argument instanceof Ratio || argument instanceof Date
            || argument instanceof DateTime || argument instanceof String || argument instanceof Time)
        {
            return true;
        }

        throw new InvalidOperatorArgument(
            "ConvertsToString(Boolean) or ConvertsToString(Long) or ConvertsToString(Integer) or ConvertsToString(Decimal) or ConvertsToString(Quantity) or ConvertsToString(Ratio) or ConvertsToString(Date) or ConvertsToString(DateTime) or ConvertsToString(Time)",
            String.format("ConvertsToString(%s)", argument.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return convertsToString(operand);
    }

}
