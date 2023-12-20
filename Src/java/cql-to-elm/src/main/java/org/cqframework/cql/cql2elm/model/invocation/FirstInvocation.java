package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.First;

public class FirstInvocation extends OperatorExpressionInvocation {
    public FirstInvocation(First expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((First) expression).getSource());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((First) expression).setSource(assertAndGetSingleOperand(operands));
    }
}
