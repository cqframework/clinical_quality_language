package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.DateTime;
import org.hl7.elm.r1.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DateTimeInvocation extends OperatorExpressionInvocation {
    public DateTimeInvocation(DateTime expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        DateTime dt = (DateTime) expression;
        List<Expression> opList = Arrays.asList(dt.getYear(), dt.getMonth(), dt.getDay(), dt.getHour(), dt.getMinute(), dt.getSecond(), dt.getMillisecond(), dt.getTimezoneOffset());
        // If the last expression is null, we should trim this down
        int i;
        for (i = 7; i > 0 && opList.get(i) == null; i--);
        return opList.subList(0, i + 1);
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ArrayList<Expression> opList = new ArrayList<>();
        for (Expression operand : operands) {
            opList.add(operand);
        }
        setDateTimeFieldsFromOperands((DateTime) expression, opList);
    }

    public static void setDateTimeFieldsFromOperands(DateTime dt, List<Expression> operands) {
        if (operands.isEmpty() || operands.size() > 8) {
            throw new IllegalArgumentException(
                    "Could not resolve call to system operator DateTime.  Expected 1 - 8 arguments.");
        }
        dt.setYear(operands.get(0));
        if (operands.size() > 1) {
            dt.setMonth(operands.get(1));
        }
        if (operands.size() > 2) {
            dt.setDay(operands.get(2));
        }
        if (operands.size() > 3) {
            dt.setHour(operands.get(3));
        }
        if (operands.size() > 4) {
            dt.setMinute(operands.get(4));
        }
        if (operands.size() > 5) {
            dt.setSecond(operands.get(5));
        }
        if (operands.size() > 6) {
            dt.setMillisecond(operands.get(6));
        }
        if (operands.size() > 7) {
            dt.setTimezoneOffset(operands.get(7));
        }
    }
}
