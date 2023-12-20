package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.UnaryExpression;

public class UnaryExpressionInvocation extends OperatorExpressionInvocation {
    public UnaryExpressionInvocation(UnaryExpression expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((UnaryExpression) expression).getOperand());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((UnaryExpression) expression).setOperand(assertAndGetSingleOperand(operands));
    }
}
