package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.AggregateExpression;
import org.hl7.elm.r1.Expression;

import java.util.Collections;

public class AggregateExpressionInvocation extends AbstractExpressionInvocation {
    public AggregateExpressionInvocation(AggregateExpression expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((AggregateExpression) expression).getSource());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((AggregateExpression) expression).setSource(assertAndGetSingleOperand(operands));
    }
}
