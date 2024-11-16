package org.cqframework.cql.cql2elm.model.invocation;

import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.TernaryExpression;

public class TernaryExpressionInvocation<T extends TernaryExpression> extends OperatorExpressionInvocation<T> {
    public TernaryExpressionInvocation(T expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return expression.getOperand();
    }

    @Override
    public void setOperands(List<Expression> operands) {
        List<Expression> expOperands = expression.getOperand();
        expOperands.clear();
        expOperands.addAll(operands);
    }
}
