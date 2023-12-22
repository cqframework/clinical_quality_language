package org.cqframework.cql.cql2elm.model.invocation;

import org.cqframework.cql.cql2elm.model.Invocation;
import org.cqframework.cql.cql2elm.model.OperatorResolution;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.Expression;

/**
 * The AbstractExpressionInvocation can be used to more simply make invocations for classes that only extend
 * Expression.
 */
public abstract class AbstractExpressionInvocation implements Invocation {
    public AbstractExpressionInvocation(Expression expression) {
        if (expression == null) {
            throw new IllegalArgumentException("expression is null.");
        }

        this.expression = expression;
    }

    protected Expression expression;

    @Override
    public void setResultType(DataType resultType) {
        expression.setResultType(resultType);
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    protected Expression assertAndGetSingleOperand(Iterable<Expression> operands) {
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

        return operand;
    }

    private OperatorResolution resolution;

    public OperatorResolution getResolution() {
        return resolution;
    }

    public void setResolution(OperatorResolution resolution) {
        this.resolution = resolution;
    }
}
