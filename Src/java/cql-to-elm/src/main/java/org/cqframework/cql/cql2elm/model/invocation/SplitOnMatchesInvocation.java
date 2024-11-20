package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Arrays;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.SplitOnMatches;

public class SplitOnMatchesInvocation extends OperatorExpressionInvocation<SplitOnMatches> {
    public SplitOnMatchesInvocation(SplitOnMatches expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Arrays.asList(expression.getStringToSplit(), expression.getSeparatorPattern());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        require(operands != null && operands.size() == 2, "SplitOnMatches operator requires two operands.");

        expression.setStringToSplit(operands.get(0));
        expression.setSeparatorPattern(operands.get(1));
    }
}
