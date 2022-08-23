package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.AnyInValueSet;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ValueSetRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bryn on 9/12/2018.
 */
public class AnyInValueSetInvocation extends OperatorExpressionInvocation {
    public AnyInValueSetInvocation(AnyInValueSet expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        List<Expression> result = new ArrayList<>();
        result.add(((AnyInValueSet)expression).getCodes());
        if (((AnyInValueSet)expression).getValuesetExpression() != null) {
            result.add(((AnyInValueSet)expression).getValuesetExpression());
        }
        return result;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        int i = 0;
        for (Expression operand : operands) {
            switch (i) {
                case 0: ((AnyInValueSet)expression).setCodes(operand); break;
                case 1: ((AnyInValueSet)expression).setValuesetExpression(operand); break;
            }
            i++;
        }

        if (i > 2) {
            throw new IllegalArgumentException("Unary or binary operator expected");
        }
    }
}
