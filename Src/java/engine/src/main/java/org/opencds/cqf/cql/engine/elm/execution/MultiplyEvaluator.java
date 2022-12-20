package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Value;

/*
*(left Integer, right Integer) Integer
*(left Long, right Long) Long
*(left Decimal, right Decimal) Decimal
*(left Decimal, right Quantity) Quantity
*(left Quantity, right Decimal) Quantity
*(left Quantity, right Quantity) Quantity

The multiply (*) operator performs numeric multiplication of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
TODO: For multiplication operations involving quantities, the resulting quantity will have the appropriate unit. For example:
12 'cm' * 3 'cm'
3 'cm' * 12 'cm2'
In this example, the first result will have a unit of 'cm2', and the second result will have a unit of 'cm3'.
If either argument is null, the result is null.
*/

public class MultiplyEvaluator extends org.cqframework.cql.elm.execution.Multiply {

  public static Object multiply(Object left, Object right) {
    if (left == null || right == null) {
        return null;
    }

    // *(Integer, Integer)
    if (left instanceof Integer) {
        return (Integer)left * (Integer)right;
    }

    if (left instanceof Long) {
        return (Long) left * (Long) right;
    }

      // *(Decimal, Decimal)
    else if (left instanceof BigDecimal && right instanceof BigDecimal) {
        return Value.verifyPrecision(((BigDecimal)left).multiply((BigDecimal)right), null);
    }

    // *(Quantity, Quantity)
    else if (left instanceof Quantity && right instanceof Quantity) {
      // TODO: unit multiplication i.e. cm*cm = cm^2
      String unit = ((Quantity) left).getUnit().equals("1") ? ((Quantity) right).getUnit() : ((Quantity) left).getUnit();
      BigDecimal value = Value.verifyPrecision((((Quantity)left).getValue()).multiply(((Quantity)right).getValue()), null);
      return new Quantity().withValue(value).withUnit(unit);
    }

    // *(Decimal, Quantity)
    else if (left instanceof BigDecimal && right instanceof Quantity) {
      BigDecimal value = Value.verifyPrecision(((BigDecimal)left).multiply(((Quantity)right).getValue()), null);
      return ((Quantity) right).withValue(value);
    }

    // *(Quantity, Decimal)
    else if (left instanceof Quantity && right instanceof BigDecimal) {
      BigDecimal value = Value.verifyPrecision((((Quantity)left).getValue()).multiply((BigDecimal)right), null);
      return ((Quantity) left).withValue(value);
    }

    // *(Uncertainty, Uncertainty)
    else if (left instanceof Interval && right instanceof Interval) {
      Interval leftInterval = (Interval)left;
      Interval rightInterval = (Interval)right;
      return new Interval(multiply(leftInterval.getStart(), rightInterval.getStart()), true, multiply(leftInterval.getEnd(), rightInterval.getEnd()), true);
    }

      throw new InvalidOperatorArgument(
          "Multiply(Integer, Integer), Multiply(Long, Long), Multiply(Decimal, Decimal), Multiply(Decimal, Quantity), Multiply(Quantity, Decimal) or Multiply(Quantity, Quantity)",
          String.format("Multiply(%s, %s)", left.getClass().getName(), right.getClass().getName())
      );
  }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        return multiply(left, right);
    }
}
