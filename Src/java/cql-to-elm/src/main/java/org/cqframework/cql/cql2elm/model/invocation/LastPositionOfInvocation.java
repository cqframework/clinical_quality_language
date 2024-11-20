package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Arrays;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.LastPositionOf;

public class LastPositionOfInvocation extends OperatorExpressionInvocation<LastPositionOf> {
    public LastPositionOfInvocation(LastPositionOf expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Arrays.asList(expression.getPattern(), expression.getString());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        require(operands != null && operands.size() == 2, "LastPositionOf operator requires two operands.");
        expression.setPattern(operands.get(0));
        expression.setString(operands.get(1));
    }
}
