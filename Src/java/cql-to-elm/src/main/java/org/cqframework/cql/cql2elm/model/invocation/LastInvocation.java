package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Last;

public class LastInvocation extends OperatorExpressionInvocation {
    public LastInvocation(Last expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((Last) expression).getSource());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((Last) expression).setSource(assertAndGetSingleOperand(operands));
    }
}
