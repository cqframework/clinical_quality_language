package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.AnyInValueSet;
import org.hl7.elm.r1.CodeSystemRef;
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
        if (((AnyInCodeSystem)expression).getCodesystem() != null) {
            result.add(((AnyInCodeSystem)expression).getCodesystem());
        }
        else {
            result.add(((AnyInCodeSystem)expression).getCodesystemExpression());
        }
        return result;
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        int i = 0;
        for (Expression operand : operands) {
            switch (i) {
                case 0: ((AnyInCodeSystem)expression).setCodes(operand); break;
                case 1:
                    if (operand instanceof CodeSystemRef) {
                        ((AnyInCodeSystem)expression).setCodesystem((CodeSystemRef)operand);
                    }
                    else {
                        ((AnyInCodeSystem)expression).setCodesystemExpression(operand);
                    }
                break;
            }
            i++;
        }

        if (i != 2) {
            throw new IllegalArgumentException("Binary operator expected");
        }
    }
}
