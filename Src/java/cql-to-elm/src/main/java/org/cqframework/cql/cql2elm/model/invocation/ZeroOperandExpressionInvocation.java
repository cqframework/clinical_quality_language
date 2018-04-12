package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.OperatorExpression;

import java.util.ArrayList;

public class ZeroOperandExpressionInvocation extends OperatorExpressionInvocation {
    public ZeroOperandExpressionInvocation(OperatorExpression expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return new ArrayList<>();
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        if (operands.iterator().hasNext()) {
            throw new IllegalArgumentException("Zero operand operation expected.");
        }
    }
}
