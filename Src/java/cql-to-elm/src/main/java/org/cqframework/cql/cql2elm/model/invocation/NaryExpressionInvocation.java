package org.cqframework.cql.cql2elm.model.invocation;

import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.NaryExpression;

public class NaryExpressionInvocation extends OperatorExpressionInvocation {
    public NaryExpressionInvocation(NaryExpression expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return ((NaryExpression) expression).getOperand();
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        List<Expression> expOperands = ((NaryExpression) expression).getOperand();
        expOperands.clear();
        for (Expression operand : operands) {
            expOperands.add(operand);
        }
    }
}
