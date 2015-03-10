package org.cqframework.cql.cql2elm.model.invocation;

import org.cqframework.cql.cql2elm.model.Invocation;
import org.cqframework.cql.elm.tracking.DataType;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.FunctionRef;

public class FunctionRefInvocation implements Invocation {
    public FunctionRefInvocation(FunctionRef expression) {
        if (expression == null) {
            throw new IllegalArgumentException("expression is null");
        }

        this.expression = expression;
    }

    private FunctionRef expression;

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
