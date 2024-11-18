package org.cqframework.cql.cql2elm.model.invocation;

import java.util.List;
import org.cqframework.cql.cql2elm.model.Invocation;
import org.cqframework.cql.cql2elm.model.OperatorResolution;
import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.Expression;

/**
 * The AbstractExpressionInvocation can be used to more simply make invocations for classes that only extend
 * Expression.
 */
abstract class AbstractExpressionInvocation<E extends Expression> implements Invocation {
    protected AbstractExpressionInvocation(E expression) {
        if (expression == null) {
            throw new IllegalArgumentException("expression is null.");
        }

        this.expression = expression;
    }

    protected E expression;

    @Override
    public void setResultType(DataType resultType) {
        expression.setResultType(resultType);
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    protected Expression assertAndGetSingleOperand(List<Expression> operands) {
        if (operands == null || operands.size() != 1) {
            throw new IllegalArgumentException("Unary operation expected.");
        }

        return operands.get(0);
    }

    private OperatorResolution resolution;

    public OperatorResolution getResolution() {
        return resolution;
    }

    public void setResolution(OperatorResolution resolution) {
        this.resolution = resolution;
    }
}
