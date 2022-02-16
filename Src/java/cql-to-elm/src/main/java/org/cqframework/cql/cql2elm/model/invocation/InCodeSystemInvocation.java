package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.AnyInCodeSystem;
import org.hl7.elm.r1.CodeSystemRef;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.InCodeSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InCodeSystemInvocation extends OperatorExpressionInvocation {
    public InCodeSystemInvocation(InCodeSystem expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        List<Expression> result = new ArrayList<>();
        result.add(((InCodeSystem)expression).getCode());
        if (((InCodeSystem)expression).getCodesystemExpression() != null) {
            result.add(((InCodeSystem)expression).getCodesystemExpression());
        }
        return result;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        int i = 0;
        for (Expression operand : operands) {
            switch (i) {
                case 0: ((InCodeSystem)expression).setCode(operand); break;
                case 1: ((InCodeSystem)expression).setCodesystemExpression(operand); break;
            }
            i++;
        }

        if (i > 2) {
            throw new IllegalArgumentException("Unary or binary operator expected");
        }
    }
}
