package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.InValueSet;
import org.hl7.elm.r1.IndexOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class IndexOfInvocation extends OperatorExpressionInvocation {
    public IndexOfInvocation(IndexOf expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        IndexOf indexOf = (IndexOf) expression;
        return Arrays.asList(indexOf.getSource(), indexOf.getElement());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Iterator<Expression> it = operands.iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("IndexOf operation requires two operands.");
        }
        IndexOf indexOf = (IndexOf) expression;
        indexOf.setSource(it.next());

        if (!it.hasNext()) {
            throw new IllegalArgumentException("IndexOf operation requires two operands.");
        }
        indexOf.setElement(it.next());

        if (it.hasNext()) {
            throw new IllegalArgumentException("IndexOf operation requires two operands.");
        }
    }
}
