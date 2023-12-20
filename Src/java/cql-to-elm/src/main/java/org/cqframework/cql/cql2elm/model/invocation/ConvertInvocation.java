package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import org.hl7.elm.r1.Convert;
import org.hl7.elm.r1.Expression;

public class ConvertInvocation extends OperatorExpressionInvocation {
    public ConvertInvocation(Convert expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((Convert) expression).getOperand());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((Convert) expression).setOperand(assertAndGetSingleOperand(operands));
    }
}
