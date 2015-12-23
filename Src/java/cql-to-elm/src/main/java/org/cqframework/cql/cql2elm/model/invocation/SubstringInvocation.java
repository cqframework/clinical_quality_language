package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Substring;

import java.util.ArrayList;
import java.util.Iterator;

public class SubstringInvocation extends AbstractExpressionInvocation {
    public SubstringInvocation(Substring expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        Substring substring = (Substring) expression;
        ArrayList<Expression> ops = new ArrayList<>();
        // Note: these casts to Expression are necessary because of bug in expression.xsd (DSTU comment #824)
        ops.add((Expression) substring.getStringToSub());
        ops.add((Expression) substring.getStartIndex());
        if (substring.getLength() != null) {
            ops.add((Expression) substring.getLength());
        }
        return ops;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Iterator<Expression> it = operands.iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("Substring operation requires two or three operands.");
        }
        Substring substring = (Substring) expression;
        substring.setStringToSub(it.next());
        if (!it.hasNext()) {
            throw new IllegalArgumentException("Substring operation requires two or three operands.");
        }
        substring.setStartIndex(it.next());
        if (it.hasNext()) {
            substring.setLength(it.next());
        }
    }
}
