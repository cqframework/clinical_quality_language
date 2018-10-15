package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Split;

import java.util.Arrays;
import java.util.Iterator;

public class SplitInvocation extends OperatorExpressionInvocation {
    public SplitInvocation(Split expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        Split split = (Split) expression;
        return Arrays.asList(split.getStringToSplit(), split.getSeparator());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Iterator<Expression> it = operands.iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("Split operation requires two operands.");
        }
        Split split = (Split) expression;
        split.setStringToSplit(it.next());
        if (!it.hasNext()) {
            throw new IllegalArgumentException("Split operation requires two operands.");
        }
        split.setSeparator(it.next());
    }
}
