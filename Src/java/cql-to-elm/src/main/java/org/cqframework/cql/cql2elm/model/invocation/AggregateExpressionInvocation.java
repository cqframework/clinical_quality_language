package org.cqframework.cql.cql2elm.model.invocation;

import java.util.Collections;
import org.hl7.elm.r1.AggregateExpression;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.TypeSpecifier;

public class AggregateExpressionInvocation extends AbstractExpressionInvocation {
    public AggregateExpressionInvocation(AggregateExpression expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((AggregateExpression) expression).getSource());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((AggregateExpression) expression).setSource(assertAndGetSingleOperand(operands));
    }

    @Override
    public Iterable<TypeSpecifier> getSignature() {
        return ((AggregateExpression) expression).getSignature();
    }

    @Override
    public void setSignature(Iterable<TypeSpecifier> signature) {
        for (TypeSpecifier typeSpecifier : signature) {
            ((AggregateExpression) expression).getSignature().add(typeSpecifier);
        }
    }
}
