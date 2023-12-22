package org.cqframework.cql.cql2elm.model.invocation;

import java.util.ArrayList;
import java.util.Iterator;
import org.hl7.elm.r1.Combine;
import org.hl7.elm.r1.Expression;

public class CombineInvocation extends OperatorExpressionInvocation {
    public CombineInvocation(Combine expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        Combine combine = (Combine) expression;
        ArrayList<Expression> ops = new ArrayList<>();
        ops.add(combine.getSource());
        if (combine.getSeparator() != null) {
            ops.add(combine.getSeparator());
        }
        return ops;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Iterator<Expression> it = operands.iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("Combine operation requires one or two operands.");
        }
        Combine combine = (Combine) expression;
        combine.setSource(it.next());

        if (it.hasNext()) {
            combine.setSeparator(it.next());
        }
    }
}
