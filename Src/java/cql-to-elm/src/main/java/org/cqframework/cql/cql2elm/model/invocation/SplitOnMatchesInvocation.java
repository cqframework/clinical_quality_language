package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.SplitOnMatches;

import java.util.Arrays;
import java.util.Iterator;

public class SplitOnMatchesInvocation extends OperatorExpressionInvocation {
    public SplitOnMatchesInvocation(SplitOnMatches expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        SplitOnMatches splitOnMatches = (SplitOnMatches) expression;
        return Arrays.asList(splitOnMatches.getStringToSplit(), splitOnMatches.getSeparatorPattern());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Iterator<Expression> it = operands.iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("SplitOnMatches operation requires two operands.");
        }
        SplitOnMatches splitOnMatches = (SplitOnMatches) expression;
        splitOnMatches.setStringToSplit(it.next());
        if (!it.hasNext()) {
            throw new IllegalArgumentException("SplitOnMatches operation requires two operands.");
        }
        splitOnMatches.setSeparatorPattern(it.next());
    }
}
