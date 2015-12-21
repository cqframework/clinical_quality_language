package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Round;

import java.util.ArrayList;
import java.util.Iterator;

public class RoundInvocation extends AbstractExpressionInvocation {
    public RoundInvocation(Round expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        Round round = (Round) expression;
        ArrayList<Expression> ops = new ArrayList<>();
        ops.add(round.getOperand());
        if (round.getPrecision() != null) {
            ops.add(round.getPrecision());
        }
        return ops;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Iterator<Expression> it = operands.iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("Round operation requires one or two operands.");
        }
        Round round = (Round) expression;
        round.setOperand(it.next());

        if (it.hasNext()) {
            round.setPrecision(it.next());
        }
    }
}
