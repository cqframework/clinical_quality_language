package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.AnyInValueSet;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.AnyInCodeSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bryn on 9/12/2018.
 */
public class AnyInCodeSystemInvocation extends OperatorExpressionInvocation {
    public AnyInCodeSystemInvocation(AnyInCodeSystem expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        List<Expression> result = new ArrayList<>();
        result.add(((AnyInCodeSystem)expression).getCodes());
        result.add(((AnyInCodeSystem)expression).getCodesystem());
        return result;
        //return Collections.singletonList(((AnyInCodeSystem) expression).getCodes());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        int i = 0;
        for (Expression operand : operands) {
            switch (i) {
                case 0: ((AnyInCodeSystem)expression).setCodes(operand); break;
                case 1: ((AnyInCodeSystem)expression).setCodesystem(operand); break;
            }
            i++;
        }

        if (i != 2) {
            throw new IllegalArgumentException("Binary operator expected");
        }
        //((AnyInCodeSystem) expression).setCodes(assertAndGetSingleOperand(operands));
    }
}
