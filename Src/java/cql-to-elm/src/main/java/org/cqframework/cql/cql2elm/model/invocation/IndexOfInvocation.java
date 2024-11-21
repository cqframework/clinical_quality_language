package org.cqframework.cql.cql2elm.model.invocation;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.IndexOf;

public class IndexOfInvocation extends OperatorExpressionInvocation<IndexOf> {
    public IndexOfInvocation(IndexOf expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Arrays.asList(expression.getSource(), expression.getElement());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        requireNonNull(operands, "operands cannot be null.");
        require(operands.size() == 2, "IndexOf operator requires two operands.");

        expression.setSource(operands.get(0));
        expression.setElement(operands.get(1));
    }
}
