package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Literal;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Quantity;

import java.math.BigDecimal;

/*
-(argument Integer) Integer
-(argument Long) Long
-(argument Decimal) Decimal
-(argument Quantity) Quantity

The negate (-) operator returns the negative of its argument.
When negating quantities, the unit is unchanged.
If the argument is null, the result is null.
*/

public class NegateEvaluator {

    public static Object negate(Object source) {
        if (source == null) {
            return null;
        }

        if (source instanceof Integer) {
            return -(int) source;
        }

        if (source instanceof Long) {
            return -(long) source;
        }

        if (source instanceof BigDecimal) {
            return ((BigDecimal) source).negate();
        }

        if (source instanceof Quantity) {
            Quantity quantity = (Quantity) source;
            return new Quantity().withValue(quantity.getValue().negate()).withUnit(quantity.getUnit());
        }

        throw new InvalidOperatorArgument(
            "Negate(Integer), Negate(Long), Negate(Decimal) or Negate(Quantity)",
            String.format("Negate(%s)", source.getClass().getName())
        );
    }

    public static Object internalEvaluate(Expression operand, State state, CqlEngine visitor) {

        // Special case to handle literals of the minimum Integer value
        // since usual implementation would try to cast 2147483648 as a
        // signed 32 bit signed integer and throw
        // java.lang.NumberFormatException: For input string: "2147483648".
        if (operand instanceof Literal && ((Literal) operand).getValue().equals("2147483648")) {
            return Integer.MIN_VALUE;
        }

        if (operand instanceof Literal && ((Literal) operand).getValue().equals("9223372036854775807")) {
            return Long.MIN_VALUE;
        }

        Object source = visitor.visitExpression(operand, state);

        return negate(source);
    }
}
