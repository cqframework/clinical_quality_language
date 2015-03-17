package org.cqframework.cql.cql2elm.model.invocation;

import org.cqframework.cql.cql2elm.model.Invocation;
import org.cqframework.cql.elm.tracking.DataType;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.InCodeSystem;

import java.util.ArrayList;
import java.util.List;

public class InCodeSystemInvocation implements Invocation {
    public InCodeSystemInvocation(InCodeSystem expression) {
        if (expression == null) {
            throw new IllegalArgumentException("expression is null.");
        }

        this.expression = expression;
    }

    private InCodeSystem expression;

    @Override
    public Iterable<Expression> getOperands() {
        List<Expression> operands = new ArrayList<>();
        operands.add(expression.getCode());
        return operands;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Expression operand = null;
        for (Expression o : operands) {
            if (operand != null) {
                throw new IllegalArgumentException("InValueSet is a unary operation.");
            }

            operand = o;
        }

        if (operand == null) {
            throw new IllegalArgumentException("InValueSet is a unary operation.");
        }

        expression.setCode(operand);
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
