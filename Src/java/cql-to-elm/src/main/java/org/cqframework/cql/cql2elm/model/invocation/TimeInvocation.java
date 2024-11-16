package org.cqframework.cql.cql2elm.model.invocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Time;

public class TimeInvocation extends OperatorExpressionInvocation<Time> {
    public TimeInvocation(Time expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        Time t = expression;
        List<Expression> opList = Arrays.asList(t.getHour(), t.getMinute(), t.getSecond(), t.getMillisecond());
        // If the last expression is null, we should trim this down
        int i;
        for (i = 3; i > 0 && opList.get(i) == null; i--)
            ;
        return opList.subList(0, i + 1);
    }

    @Override
    public void setOperands(List<Expression> operands) {
        var opList = new ArrayList<>(operands);
        setTimeFieldsFromOperands(expression, opList);
    }

    public static void setTimeFieldsFromOperands(Time t, List<Expression> operands) {
        if (operands.isEmpty() || operands.size() > 4) {
            throw new IllegalArgumentException(
                    "Could not resolve call to system operator Time.  Expected 1 - 4 arguments.");
        }

        t.setHour(operands.get(0));
        if (operands.size() > 1) {
            t.setMinute(operands.get(1));
        }
        if (operands.size() > 2) {
            t.setSecond(operands.get(2));
        }
        if (operands.size() > 3) {
            t.setMillisecond(operands.get(3));
        }
    }
}
