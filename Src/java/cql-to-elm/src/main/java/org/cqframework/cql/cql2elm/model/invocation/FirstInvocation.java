package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.First;

public class FirstInvocation extends OperatorExpressionInvocation<First> {
    public FirstInvocation(First expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Collections.singletonList(expression.getSource());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        expression.setSource(requireSingleton(operands));
    }
}
