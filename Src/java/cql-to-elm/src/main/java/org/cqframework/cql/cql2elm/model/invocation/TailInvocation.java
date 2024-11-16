package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Slice;

/**
 * Created by Bryn on 5/17/2017.
 */
public class TailInvocation extends OperatorExpressionInvocation<Slice> {
    public TailInvocation(Slice expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Collections.singletonList(expression.getSource());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        expression.setSource(assertAndGetSingleOperand(operands));
    }
}
