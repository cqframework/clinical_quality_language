package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Slice;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Bryn on 5/17/2017.
 */
public class TailInvocation extends OperatorExpressionInvocation {
    public TailInvocation(Slice expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((Slice) expression).getSource());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((Slice) expression).setSource(assertAndGetSingleOperand(operands));
    }
}
