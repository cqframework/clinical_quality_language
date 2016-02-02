package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.BinaryExpression;
import org.hl7.elm.r1.Expression;

import java.util.List;


public class BinaryExpressionInvocation extends AbstractExpressionInvocation {
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
