package org.cqframework.cql.cql2elm.model.invocation;

import java.util.ArrayList;
import java.util.List;
import org.hl7.elm.r1.AnyInValueSet;
import org.hl7.elm.r1.Expression;

/**
 * Created by Bryn on 9/12/2018.
 */
public class AnyInValueSetInvocation extends OperatorExpressionInvocation<AnyInValueSet> {
    public AnyInValueSetInvocation(AnyInValueSet expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        List<Expression> result = new ArrayList<>();
        result.add(expression.getCodes());
        if (expression.getValuesetExpression() != null) {
            result.add(expression.getValuesetExpression());
        }
        return result;
    }

    @Override
    public void setOperands(List<Expression> operands) {
        if (operands == null || operands.isEmpty() || operands.size() > 2) {
            throw new IllegalArgumentException("AnyInValueSet operation requires one or two operands.");
        }

        expression.setCodes(operands.get(0));
        if (operands.size() > 1) {
            expression.setValuesetExpression(operands.get(1));
        }
    }
}
