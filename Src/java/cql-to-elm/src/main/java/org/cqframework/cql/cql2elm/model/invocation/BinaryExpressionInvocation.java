package org.cqframework.cql.cql2elm.model.invocation;

import java.util.List;
import org.hl7.elm.r1.BinaryExpression;
import org.hl7.elm.r1.Expression;

public class BinaryExpressionInvocation extends OperatorExpressionInvocation {
    public BinaryExpressionInvocation(BinaryExpression expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return ((BinaryExpression) expression).getOperand();
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        List<Expression> expOperands = ((BinaryExpression) expression).getOperand();
        expOperands.clear();
        for (Expression operand : operands) {
            expOperands.add(operand);
        }
    }
}
