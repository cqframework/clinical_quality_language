package org.cqframework.cql.cql2elm.model.invocation;

import java.util.ArrayList;
import java.util.List;
import org.hl7.elm.r1.Combine;
import org.hl7.elm.r1.Expression;

public class CombineInvocation extends OperatorExpressionInvocation<Combine> {
    public CombineInvocation(Combine expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        Combine combine = expression;
        ArrayList<Expression> ops = new ArrayList<>();
        ops.add(combine.getSource());
        if (combine.getSeparator() != null) {
            ops.add(combine.getSeparator());
        }
        return ops;
    }

    @Override
    public void setOperands(List<Expression> operands) {
        if (operands == null || operands.isEmpty() || operands.size() > 2) {
            throw new IllegalArgumentException("Combine operation requires one or two operands.");
        }

        expression.setSource(operands.get(0));
        if (operands.size() > 1) {
            expression.setSeparator(operands.get(1));
        }
    }
}
