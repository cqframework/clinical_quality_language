package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Date;
import org.hl7.elm.r1.DateTime;
import org.hl7.elm.r1.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DateInvocation extends OperatorExpressionInvocation {
    public DateInvocation(Date expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        Date dt = (Date) expression;
        List<Expression> opList = Arrays.asList(dt.getYear(), dt.getMonth(), dt.getDay());
        // If the last expression is null, we should trim this down
        int i;
        for (i = 2; i > 0 && opList.get(i) == null; i--);
        return opList.subList(0, i + 1);
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ArrayList<Expression> opList = new ArrayList<>();
        for (Expression operand : operands) {
            opList.add(operand);
        }
        setDateFieldsFromOperands((Date) expression, opList);
    }

    public static void setDateFieldsFromOperands(Date dt, List<Expression> operands) {
        if (operands.isEmpty() || operands.size() > 3) {
            throw new IllegalArgumentException(
                    "Could not resolve call to system operator DateTime.  Expected 1 - 3 arguments.");
        }
        dt.setYear(operands.get(0));
        if (operands.size() > 1) {
            dt.setMonth(operands.get(1));
        }
        if (operands.size() > 2) {
            dt.setDay(operands.get(2));
        }
    }
}
