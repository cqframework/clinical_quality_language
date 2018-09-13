package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.AnyInCodeSystem;

import java.util.Collections;

/**
 * Created by Bryn on 9/12/2018.
 */
public class AnyInCodeSystemInvocation extends OperatorExpressionInvocation {
    public AnyInCodeSystemInvocation(AnyInCodeSystem expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((AnyInCodeSystem) expression).getCodes());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((AnyInCodeSystem) expression).setCodes(assertAndGetSingleOperand(operands));
    }
}
