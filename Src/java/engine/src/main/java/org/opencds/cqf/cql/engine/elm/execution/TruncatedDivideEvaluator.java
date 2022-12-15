package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
div(left Integer, right Integer) Integer
div(left Decimal, right Decimal) Decimal
div(left Quantity, right Quantity) Quantity

The div operator performs truncated division of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/

public class TruncatedDivideEvaluator extends org.cqframework.cql.elm.execution.TruncatedDivide {

    public static Object div(Object left, Object right, Context context) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Integer) {
            if ((Integer)right == 0) {
                return null;
            }

            return (Integer)left / (Integer)right;
        }

        else if (left instanceof BigDecimal) {
            if (EqualEvaluator.equal(right, new BigDecimal("0.0"), context)) {
                return null;
            }

            return ((BigDecimal)left).divideAndRemainder((BigDecimal)right)[0];
        }

        else if (left instanceof Quantity) {
            if (EqualEvaluator.equal(((Quantity) right).getValue(), new BigDecimal("0.0"), context)) {
                return null;
            }
            return new Quantity()
                .withUnit(((Quantity) left).getUnit())
                .withValue(((Quantity) left).getValue().divideAndRemainder(((Quantity) right).getValue())[0]);
        }

        else if (left instanceof Interval && right instanceof Interval) {
            Interval leftInterval = (Interval)left;
            Interval rightInterval = (Interval)right;

            return new Interval(div(leftInterval.getStart(), rightInterval.getStart(), context), true, div(leftInterval.getEnd(), rightInterval.getEnd(), context), true);
        }

        throw new InvalidOperatorArgument(
            "TruncatedDivide(Integer, Integer), TruncatedDivide(Decimal, Decimal),  TruncatedDivide(Quantity, Quantity)",
            String.format("TruncatedDivide(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        return div(left, right, context);
    }
}
