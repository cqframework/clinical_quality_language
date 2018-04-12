package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Slice;

import java.util.ArrayList;

/**
 * Created by Bryn on 5/17/2017.
 */
public class SkipInvocation extends OperatorExpressionInvocation {
    public SkipInvocation(Slice expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        ArrayList result = new ArrayList();
        result.add(((Slice)expression).getSource());
        result.add(((Slice)expression).getStartIndex());
        return result;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        boolean first = true;
        for (Expression operand : operands) {
            if (first) {
                ((Slice)expression).setSource(operand);
                first = false;
            }
            else {
                ((Slice)expression).setStartIndex(operand);
            }
        }
    }
}
