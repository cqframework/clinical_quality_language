package org.cqframework.cql.cql2elm.model.invocation;

import java.util.ArrayList;
import java.util.List;
import org.hl7.elm.r1.AnyInCodeSystem;
import org.hl7.elm.r1.Expression;

/**
 * Created by Bryn on 9/12/2018.
 */
public class AnyInCodeSystemInvocation extends OperatorExpressionInvocation<AnyInCodeSystem> {
    public AnyInCodeSystemInvocation(AnyInCodeSystem expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        List<Expression> result = new ArrayList<>();
        result.add(expression.getCodes());
        if (expression.getCodesystemExpression() != null) {
            result.add(expression.getCodesystemExpression());
        }
        return result;
    }

    @Override
    public void setOperands(List<Expression> operands) {
        if (operands == null || operands.isEmpty() || operands.size() > 2) {
            throw new IllegalArgumentException("AnyInCodeSystem operation requires one or two operands.");
        }

        expression.setCodes(operands.get(0));
        if (operands.size() > 1) {
            expression.setCodesystemExpression(operands.get(1));
        }
    }
}
