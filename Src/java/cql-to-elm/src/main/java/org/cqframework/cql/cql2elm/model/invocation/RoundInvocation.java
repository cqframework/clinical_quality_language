package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Round;

import java.util.Collections;

public class RoundInvocation extends AbstractExpressionInvocation {
    public RoundInvocation(Round expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((Round) expression).getOperand());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((Round) expression).setOperand(assertAndGetSingleOperand(operands));
    }
}
