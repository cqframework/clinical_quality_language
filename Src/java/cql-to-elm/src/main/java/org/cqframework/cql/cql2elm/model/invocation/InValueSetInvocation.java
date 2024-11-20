package org.cqframework.cql.cql2elm.model.invocation;

import java.util.ArrayList;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.InValueSet;

public class InValueSetInvocation extends OperatorExpressionInvocation<InValueSet> {
    public InValueSetInvocation(InValueSet expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        List<Expression> result = new ArrayList<>();
        result.add(expression.getCode());
        if (expression.getValuesetExpression() != null) {
            result.add(expression.getValuesetExpression());
        }
        return result;
    }

    @Override
    public void setOperands(List<Expression> operands) {
        require(
                operands != null && !operands.isEmpty() && operands.size() <= 2,
                "InValueSet operator requires one or two operands.");
        expression.setCode(operands.get(0));
        if (operands.size() > 1) {
            expression.setValuesetExpression(operands.get(1));
        }
    }
}
