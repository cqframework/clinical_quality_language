package org.cqframework.cql.cql2elm.model.invocation;

import java.util.ArrayList;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Substring;

public class SubstringInvocation extends OperatorExpressionInvocation<Substring> {
    public SubstringInvocation(Substring expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        ArrayList<Expression> ops = new ArrayList<>();
        ops.add(expression.getStringToSub());
        ops.add(expression.getStartIndex());
        if (expression.getLength() != null) {
            ops.add(expression.getLength());
        }
        return ops;
    }

    @Override
    public void setOperands(List<Expression> operands) {
        require(
                operands != null && operands.size() >= 2 && operands.size() <= 3,
                "Substring operator requires two or three operands.");

        expression.setStringToSub(operands.get(0));
        expression.setStartIndex(operands.get(1));
        if (operands.size() > 2) {
            expression.setLength(operands.get(2));
        }
    }
}
