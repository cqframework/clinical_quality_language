package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.InValueSet;
import org.hl7.elm.r1.ValueSetRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InValueSetInvocation extends OperatorExpressionInvocation {
    public InValueSetInvocation(InValueSet expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        List<Expression> result = new ArrayList<>();
        result.add(((InValueSet)expression).getCode());
        if (((InValueSet)expression).getValueset() instanceof ValueSetRef) {
            result.add(((InValueSet)expression).getValueset());
        }
        else {
            result.add(((InValueSet)expression).getValuesetEx());
        }
        return result;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        int i = 0;
        for (Expression operand : operands) {
            switch (i) {
                case 0: ((InValueSet)expression).setCode(operand); break;
                case 1:
                    if (operand instanceof ValueSetRef) {
                        ((InValueSet)expression).setValueset((ValueSetRef)operand);
                    }
                    else {
                        ((InValueSet)expression).setValuesetEx(operand);
                    }
                break;
            }
            i++;
        }

        if (i != 2) {
            throw new IllegalArgumentException("Binary operator expected");
        }
        //((InValueSet) expression).setCode(assertAndGetSingleOperand(operands));
    }
}
