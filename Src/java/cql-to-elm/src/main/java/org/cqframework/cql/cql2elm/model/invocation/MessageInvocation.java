package org.cqframework.cql.cql2elm.model.invocation;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Message;

public class MessageInvocation extends OperatorExpressionInvocation<Message> {
    public MessageInvocation(Message expression) {
        super(expression);
    }

    @Override
    public List<Expression> getOperands() {
        return Arrays.asList(
                expression.getSource(),
                expression.getCondition(),
                expression.getCode(),
                expression.getSeverity(),
                expression.getMessage());
    }

    @Override
    public void setOperands(List<Expression> operands) {
        requireNonNull(operands, "operands cannot be null.");
        require(operands.size() == 5, "Message operator requires five operands.");
        expression.setSource(operands.get(0));
        expression.setCondition(operands.get(1));
        expression.setCode(operands.get(2));
        expression.setSeverity(operands.get(3));
        expression.setMessage(operands.get(4));
    }
}
