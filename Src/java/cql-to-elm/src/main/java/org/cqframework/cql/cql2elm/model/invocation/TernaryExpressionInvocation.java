package org.cqframework.cql.cql2elm.model.invocation;

import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.TernaryExpression;

public class TernaryExpressionInvocation extends OperatorExpressionInvocation {
    public TernaryExpressionInvocation(TernaryExpression expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return ((TernaryExpression) expression).getOperand();
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        List<Expression> expOperands = ((TernaryExpression) expression).getOperand();
        expOperands.clear();
        for (Expression operand : operands) {
            expOperands.add(operand);
        }
    }
}
