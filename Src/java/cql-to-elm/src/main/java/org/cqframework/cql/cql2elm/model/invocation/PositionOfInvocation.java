package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.PositionOf;
import org.hl7.elm.r1.Split;

import java.util.Arrays;
import java.util.Iterator;

public class PositionOfInvocation extends OperatorExpressionInvocation {
    public PositionOfInvocation(PositionOf expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        PositionOf pos = (PositionOf) expression;
        return Arrays.asList(pos.getPattern(), pos.getString());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Iterator<Expression> it = operands.iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("PositionOf operation requires two operands.");
        }
        PositionOf pos = (PositionOf) expression;
        pos.setPattern(it.next());
        if (!it.hasNext()) {
            throw new IllegalArgumentException("PositionOf operation requires two operands.");
        }
        pos.setString(it.next());
    }
}
