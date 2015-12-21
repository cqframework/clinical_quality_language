package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.InValueSet;

import java.util.Collections;

public class InValueSetInvocation extends AbstractExpressionInvocation {
    public InValueSetInvocation(InValueSet expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((InValueSet) expression).getCode());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((InValueSet) expression).setCode(assertAndGetSingleOperand(operands));
    }
}
