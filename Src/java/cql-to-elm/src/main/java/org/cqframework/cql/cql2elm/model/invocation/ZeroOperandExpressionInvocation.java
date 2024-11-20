package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.OperatorExpression;

public class ZeroOperandExpressionInvocation extends OperatorExpressionInvocation<OperatorExpression> {
    public ZeroOperandExpressionInvocation(OperatorExpression expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Collections.emptyList();
    }

    @Override
    public void setOperands(List<Expression> operands) {
        require(operands != null && operands.isEmpty(), "Zero operand operator expected.");
    }
}
