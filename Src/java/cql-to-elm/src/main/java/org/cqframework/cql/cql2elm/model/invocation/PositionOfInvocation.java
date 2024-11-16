package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Arrays;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.PositionOf;

public class PositionOfInvocation extends OperatorExpressionInvocation<PositionOf> {
    public PositionOfInvocation(PositionOf expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Arrays.asList(expression.getPattern(), expression.getString());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        if (operands == null || operands.size() != 2) {
            throw new IllegalArgumentException("PositionOf operation requires two operands.");
        }

        expression.setPattern(operands.get(0));
        expression.setString(operands.get(1));
    }
}
