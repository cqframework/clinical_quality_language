package org.cqframework.cql.cql2elm.model.invocation;

import static java.util.Objects.requireNonNull;

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
        requireNonNull(operands, "operands cannot be null.");
        require(!operands.isEmpty() && operands.size() <= 2, "AnyInCodeSystem operator requires one or two operands.");

        expression.setCodes(operands.get(0));
        if (operands.size() > 1) {
            expression.setCodesystemExpression(operands.get(1));
        }
    }
}
