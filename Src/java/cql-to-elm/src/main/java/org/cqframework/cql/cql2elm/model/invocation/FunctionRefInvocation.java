package org.cqframework.cql.cql2elm.model.invocation;

import java.util.List;
import org.cqframework.cql.cql2elm.model.OperatorResolution;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.TypeSpecifier;

public class FunctionRefInvocation extends AbstractExpressionInvocation {
    public FunctionRefInvocation(FunctionRef expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return ((FunctionRef) expression).getOperand();
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        List<Expression> expOperands = ((FunctionRef) expression).getOperand();
        expOperands.clear();
        for (Expression operand : operands) {
            expOperands.add(operand);
        }
    }

    @Override
    public Iterable<TypeSpecifier> getSignature() {
        return ((FunctionRef) expression).getSignature();
    }

    @Override
    public void setSignature(Iterable<TypeSpecifier> signature) {
        for (TypeSpecifier typeSpecifier : signature) {
            ((FunctionRef) expression).getSignature().add(typeSpecifier);
        }
    }

    @Override
    public void setResolution(OperatorResolution resolution) {
        super.setResolution(resolution);
        FunctionRef fr = (FunctionRef) expression;
        if (resolution.getLibraryName() != null && !resolution.getLibraryName().equals(fr.getLibraryName())) {
            fr.setLibraryName(resolution.getLibraryName());
        }
    }
}
