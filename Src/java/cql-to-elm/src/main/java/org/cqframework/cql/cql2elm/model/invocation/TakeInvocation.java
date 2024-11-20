package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Arrays;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Slice;

/**
 * Created by Bryn on 5/17/2017.
 */
public class TakeInvocation extends OperatorExpressionInvocation<Slice> {
    public TakeInvocation(Slice expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Arrays.asList(expression.getSource(), expression.getEndIndex());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        require(operands != null && operands.size() == 2, "Take operator requires two operands.");
        expression.setSource(operands.get(0));
        expression.setEndIndex(operands.get(1));
    }
}
