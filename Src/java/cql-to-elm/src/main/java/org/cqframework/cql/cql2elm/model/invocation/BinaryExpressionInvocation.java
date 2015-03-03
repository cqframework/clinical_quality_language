package org.cqframework.cql.cql2elm.model.invocation;

import org.cqframework.cql.cql2elm.model.Invocation;
import org.cqframework.cql.elm.tracking.DataType;
import org.hl7.elm.r1.BinaryExpression;
import org.hl7.elm.r1.Expression;


public class BinaryExpressionInvocation implements Invocation {
    public BinaryExpressionInvocation(BinaryExpression expression) {
        if (expression == null) {
            throw new IllegalArgumentException("expression is null.");
        }

        this.expression = expression;
    }

    private BinaryExpression expression;

    @Override
    public Iterable<Expression> getOperands() {
        return expression.getOperand();
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        expression.getOperand().clear();
        for (Expression operand : operands) {
            expression.getOperand().add(operand);
        }
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
