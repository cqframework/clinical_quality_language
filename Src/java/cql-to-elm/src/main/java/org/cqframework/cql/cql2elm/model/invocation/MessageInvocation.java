package org.cqframework.cql.cql2elm.model.invocation;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Message;

import java.util.Arrays;
import java.util.Iterator;

public class MessageInvocation extends AbstractExpressionInvocation {
    public MessageInvocation(Message expression) {
        super(expression);
    }

    @Override
    public Iterable<Expression> getOperands() {
        Message message = (Message) expression;
        return Arrays.asList(message.getSource(), message.getCondition(), message.getCode(), message.getSeverity(), message.getMessage());
    }

    @Override
    public void setOperands(Iterable<Expression> operands) {
        Message message = (Message)expression;

        int i = 0;
        for (Expression operand : operands) {
            switch (i) {
                case 0: message.setSource(operand); break;
                case 1: message.setCondition(operand); break;
                case 2: message.setCode(operand); break;
                case 3: message.setSeverity(operand); break;
                case 4: message.setMessage(operand); break;
                default: throw new IllegalArgumentException("Message operation requires five operands.");
            }
            i++;
        }
    }
}
