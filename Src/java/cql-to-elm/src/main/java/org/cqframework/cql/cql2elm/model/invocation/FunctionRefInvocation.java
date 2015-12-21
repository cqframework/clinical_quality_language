package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.FunctionRef;

import java.util.List;

public class FunctionRefInvocation extends AbstractExpressionInvocation {
    public FunctionRefInvocation(FunctionRef expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return ((FunctionRef) expression).getOperand();
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        List<Expression> expOperands = ((FunctionRef) expression).getOperand();
        expOperands.clear();
        for (Expression operand : operands) {
            expOperands.add(operand);
        }
    }
}
