package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Arrays;
import java.util.List;
import org.hl7.elm.r1.Date;
import org.hl7.elm.r1.Expression;

public class DateInvocation extends OperatorExpressionInvocation<Date> {
    public DateInvocation(Date expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        Date dt = expression;
        List<Expression> opList = Arrays.asList(dt.getYear(), dt.getMonth(), dt.getDay());
        // If the last expression is null, we should trim this down
        int i;
        for (i = 2; i > 0 && opList.get(i) == null; i--)
            ;
        return opList.subList(0, i + 1);
    }

    @Override
    public void setOperands(List<Expression> operands) {
        setDateFieldsFromOperands(expression, operands);
    }

    public static void setDateFieldsFromOperands(Date dt, List<Expression> operands) {
        require(
                operands != null && !operands.isEmpty() && operands.size() <= 3,
                "Date operator requires one to three operands.");

        dt.setYear(operands.get(0));
        if (operands.size() > 1) {
            dt.setMonth(operands.get(1));
        }
        if (operands.size() > 2) {
            dt.setDay(operands.get(2));
        }
    }
}
