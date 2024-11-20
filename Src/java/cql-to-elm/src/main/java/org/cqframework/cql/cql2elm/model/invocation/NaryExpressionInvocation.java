package org.cqframework.cql.cql2elm.model.invocation;

import static java.util.Objects.requireNonNull;

import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.NaryExpression;

public class NaryExpressionInvocation extends OperatorExpressionInvocation<NaryExpression> {
    public NaryExpressionInvocation(NaryExpression expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return expression.getOperand();
    }

    @Override
    public void setOperands(List<Expression> operands) {
        requireNonNull(operands, "NaryExpression requires operands.");
        expression.setOperand(operands);
    }
}
