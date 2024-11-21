package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.UnaryExpression;

public class UnaryExpressionInvocation<U extends UnaryExpression> extends OperatorExpressionInvocation<U> {
    public UnaryExpressionInvocation(U expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Collections.singletonList(expression.getOperand());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        expression.setOperand(requireSingleton(operands));
    }
}
