package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.InCodeSystem;

import java.util.Collections;

public class InCodeSystemInvocation extends OperatorExpressionInvocation {
    public InCodeSystemInvocation(InCodeSystem expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((InCodeSystem) expression).getCode());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((InCodeSystem) expression).setCode(assertAndGetSingleOperand(operands));
    }
}
