package org.opencds.cqf.cql.engine.elm.executing;

import java.util.stream.StreamSupport;
import org.hl7.elm.r1.As;
import org.hl7.elm.r1.Length;
import org.hl7.elm.r1.NamedTypeSpecifier;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;

/*
*** LIST NOTES ***
Length(argument List<T>) Integer

The Length operator returns the number of elements in a list.
If the argument is null, the result is 0.

*** STRING NOTES ***
Length(argument String) Integer

The Length operator returns the number of characters in a string.
If the argument is null, the result is null.
*/

public class LengthEvaluator {

    public static Object length(Object operand) {
        if (operand instanceof String) {
            return stringLength((String) operand);
        }

        if (operand instanceof Iterable) {
            return listLength((Iterable<?>) operand);
        }

        throw new InvalidOperatorArgument(
                "Length(List<T>) or Length(String)",
                String.format("Length(%s)", operand.getClass().getName()));
    }

    public static Integer stringLength(String operand) {
        if (operand == null) {
            return null;
        }

        return operand.length();
    }

    public static Integer listLength(Iterable<?> operand) {
        if (operand == null) {
            return 0;
        }

        return (int) StreamSupport.stream(((Iterable<?>) operand).spliterator(), false)
                .count();
    }

    public static Object internalEvaluate(Object operand, Length length, State state) {

        // null operand case
        if (length.getOperand() instanceof As) {
            if (((As) length.getOperand()).getAsTypeSpecifier() instanceof NamedTypeSpecifier) {
                return stringLength((String) operand);
            } else {
                return listLength((Iterable<?>) operand);
            }
        }

        return length(operand);
    }
}
