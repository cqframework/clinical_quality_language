package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Arrays;
import java.util.Iterator;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.LastPositionOf;

public class LastPositionOfInvocation extends OperatorExpressionInvocation {
    public LastPositionOfInvocation(LastPositionOf expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        LastPositionOf pos = (LastPositionOf) expression;
        return Arrays.asList(pos.getPattern(), pos.getString());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Iterator<Expression> it = operands.iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("LastPositionOf operation requires two operands.");
        }
        LastPositionOf pos = (LastPositionOf) expression;
        pos.setPattern(it.next());
        if (!it.hasNext()) {
            throw new IllegalArgumentException("LastPositionOf operation requires two operands.");
        }
        pos.setString(it.next());
    }
}
