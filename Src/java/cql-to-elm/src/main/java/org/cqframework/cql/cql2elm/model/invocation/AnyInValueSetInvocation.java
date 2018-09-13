package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.AnyInValueSet;
import org.hl7.elm.r1.Expression;

import java.util.Collections;

/**
 * Created by Bryn on 9/12/2018.
 */
public class AnyInValueSetInvocation extends OperatorExpressionInvocation {
    public AnyInValueSetInvocation(AnyInValueSet expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        return Collections.singletonList(((AnyInValueSet) expression).getCodes());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        ((AnyInValueSet) expression).setCodes(assertAndGetSingleOperand(operands));
    }
}
