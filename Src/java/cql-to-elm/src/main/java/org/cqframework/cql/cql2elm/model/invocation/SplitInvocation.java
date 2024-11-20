package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Arrays;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Split;

public class SplitInvocation extends OperatorExpressionInvocation<Split> {
    public SplitInvocation(Split expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Arrays.asList(expression.getStringToSplit(), expression.getSeparator());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        require(operands != null && operands.size() == 2, "Split operator requires two operands.");

        expression.setStringToSplit(operands.get(0));
        expression.setSeparator(operands.get(1));
    }
}
