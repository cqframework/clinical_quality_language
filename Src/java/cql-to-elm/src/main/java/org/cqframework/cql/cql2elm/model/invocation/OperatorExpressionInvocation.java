package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.OperatorExpression;
import org.hl7.elm.r1.TypeSpecifier;


/**
 * Created by Bryn on 4/12/2018.
 */
public abstract class OperatorExpressionInvocation extends AbstractExpressionInvocation {
    public OperatorExpressionInvocation(OperatorExpression expression) {
        super(expression);
    }

    @Override
    public Iterable<TypeSpecifier> getSignature() {
        return ((OperatorExpression)expression).getSignature();
    }

    @Override
    public void setSignature(Iterable<TypeSpecifier> signature) {
        for (TypeSpecifier typeSpecifier : signature) {
            ((OperatorExpression)expression).getSignature().add(typeSpecifier);
        }
    }
}
