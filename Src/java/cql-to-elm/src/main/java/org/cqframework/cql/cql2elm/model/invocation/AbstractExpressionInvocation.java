package org.cqframework.cql.cql2elm.model.invocation;

import static java.util.Objects.requireNonNull;

import java.util.List;
import org.apache.commons.lang3.Validate;
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
        this.expression = requireNonNull(expression, "expression is null.");
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

    protected static void require(boolean condition, String message) {
        Validate.isTrue(condition, message);
    }

    protected Expression requireSingleton(List<Expression> operands) {
        requireNonNull(operands, "operands cannot be null.");
        require(operands.size() == 1, "Unary operator expected.");
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
