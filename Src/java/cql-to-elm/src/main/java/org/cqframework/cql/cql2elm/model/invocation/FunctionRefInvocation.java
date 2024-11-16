package org.cqframework.cql.cql2elm.model.invocation;

import java.util.List;
import org.cqframework.cql.cql2elm.model.OperatorResolution;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.TypeSpecifier;

public class FunctionRefInvocation extends AbstractExpressionInvocation<FunctionRef> {
    public FunctionRefInvocation(FunctionRef expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return expression.getOperand();
    }

    @Override
    public void setOperands(List<Expression> operands) {
        List<Expression> expOperands = expression.getOperand();
        expOperands.clear();
        expOperands.addAll(operands);
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

    @Override
    public void setResolution(OperatorResolution resolution) {
        super.setResolution(resolution);
        FunctionRef fr = expression;
        if (resolution.getLibraryName() != null && !resolution.getLibraryName().equals(fr.getLibraryName())) {
            fr.setLibraryName(resolution.getLibraryName());
        }
    }
}
