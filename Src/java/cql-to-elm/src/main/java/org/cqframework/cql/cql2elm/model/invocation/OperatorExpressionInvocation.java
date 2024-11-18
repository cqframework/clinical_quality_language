package org.cqframework.cql.cql2elm.model.invocation;

import java.util.List;
import org.hl7.elm.r1.OperatorExpression;
import org.hl7.elm.r1.TypeSpecifier;

/**
 * Created by Bryn on 4/12/2018.
 */
@SuppressWarnings("checkstyle:abstractclassname")
public abstract class OperatorExpressionInvocation<O extends OperatorExpression>
        extends AbstractExpressionInvocation<O> {
    protected OperatorExpressionInvocation(O expression) {
        super(expression);
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
