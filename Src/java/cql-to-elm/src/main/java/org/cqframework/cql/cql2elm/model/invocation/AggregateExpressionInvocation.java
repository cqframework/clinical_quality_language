package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import java.util.List;
import org.hl7.elm.r1.AggregateExpression;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.TypeSpecifier;

public class AggregateExpressionInvocation<A extends AggregateExpression> extends AbstractExpressionInvocation<A> {
    public AggregateExpressionInvocation(A expression) {
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

    @Override
    public List<TypeSpecifier> getSignature() {
        return expression.getSignature();
    }

    @Override
    public void setSignature(List<TypeSpecifier> signature) {
        for (TypeSpecifier typeSpecifier : signature) {
            expression.getSignature().add(typeSpecifier);
        }
    }
}
