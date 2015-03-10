package org.cqframework.cql.cql2elm.model.invocation;

import org.cqframework.cql.cql2elm.model.Invocation;
import org.cqframework.cql.elm.tracking.DataType;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.UnaryExpression;

import java.util.ArrayList;
import java.util.List;

public class UnaryExpressionInvocation implements Invocation {
    public UnaryExpressionInvocation(UnaryExpression expression) {
        if (expression == null) {
            throw new IllegalArgumentException("expression is null.");
        }

        this.expression = expression;
    }

    private UnaryExpression expression;

    @Override
    public Iterable<Expression> getOperands() {
        List<Expression> operands = new ArrayList<>();
        operands.add(expression.getOperand());
        return operands;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Expression operand = null;
        for (Expression o : operands) {
            if (operand != null) {
                throw new IllegalArgumentException("Unary operation expected.");
            }

            operand = o;
        }

        if (operand == null) {
            throw new IllegalArgumentException("Unary operation expected.");
        }

        expression.setOperand(operand);
    }

    @Override
    public void setResultType(DataType resultType) {
        expression.setResultType(resultType);
    }

    @Override
    public Expression getExpression() {
        return expression;
    }
}
