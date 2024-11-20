package org.cqframework.cql.cql2elm.model.invocation;

import java.util.ArrayList;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Round;

public class RoundInvocation extends OperatorExpressionInvocation<Round> {
    public RoundInvocation(Round expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        var ops = new ArrayList<Expression>();
        ops.add(expression.getOperand());
        if (expression.getPrecision() != null) {
            ops.add(expression.getPrecision());
        }
        return ops;
    }

    @Override
    public void setOperands(List<Expression> operands) {
        require(
                operands != null && !operands.isEmpty() && operands.size() <= 2,
                "Round operator requires one or two operands.");
        expression.setOperand(operands.get(0));
        if (operands.size() > 1) {
            expression.setPrecision(operands.get(1));
        }
    }
}
