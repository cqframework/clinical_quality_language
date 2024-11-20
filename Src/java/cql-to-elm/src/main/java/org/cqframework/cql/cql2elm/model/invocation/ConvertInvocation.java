package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import java.util.List;
import org.hl7.elm.r1.Convert;
import org.hl7.elm.r1.Expression;

public class ConvertInvocation extends OperatorExpressionInvocation<Convert> {
    public ConvertInvocation(Convert expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Collections.singletonList(expression.getOperand());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        expression.setOperand(requireSingleton(operands));
    }
}
