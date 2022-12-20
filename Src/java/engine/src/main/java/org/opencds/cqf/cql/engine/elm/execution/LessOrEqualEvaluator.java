package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
<=(left Integer, right Integer) Boolean
<=(left Long, right Long) Boolean
<=(left Decimal, right Decimal) Boolean
<=(left Quantity, right Quantity) Boolean
<=(left Date, right Date) Boolean
<=(left DateTime, right DateTime) Boolean
<=(left Time, right Time) Boolean
<=(left String, right String) Boolean

The less or equal (<=) operator returns true if the first argument is less than or equal to the second argument.

String comparisons are strictly lexical based on the Unicode value of the individual characters in the string.

For comparisons involving quantities, the dimensions of each quantity must be the same, but not necessarily the unit.
    For example, units of 'cm' and 'm' are comparable, but units of 'cm2' and 'cm' are not. Attempting to operate on
    quantities with invalid units will result in a null. When a quantity has no units specified, it is treated as a
    quantity with the default unit ('1').

For date/time values, the comparison is performed by considering each precision in order, beginning with years
    (or hours for time values). If the values are the same, comparison proceeds to the next precision; if the
    first value is less than the second, the result is true; if the first value is greater than the second, the
    result is false; if one input has a value for the precision and the other does not, the comparison stops and
    the result is null; if neither input has a value for the precision or the last precision has been reached,
    the comparison stops and the result is true.
*/

public class LessOrEqualEvaluator extends org.cqframework.cql.elm.execution.LessOrEqual {

  public static Boolean lessOrEqual(Object left, Object right, Context context) {
    if (left == null || right == null) {
        return null;
    }

      if (left instanceof Integer && right instanceof Integer) {
          return ((Integer) left).compareTo((Integer) right) <= 0;
      }

      if (left instanceof Long && right instanceof Long) {
          return ((Long) left).compareTo((Long) right) <= 0;
      }

      else if (left instanceof BigDecimal && right instanceof BigDecimal) {
          return ((BigDecimal) left).compareTo((BigDecimal) right) <= 0;
      }

      else if (left instanceof Quantity && right instanceof Quantity) {
          if (((Quantity) left).getValue() == null || ((Quantity) right).getValue() == null) {
              return null;
          }
          Integer nullableCompareTo = ((Quantity)left).nullableCompareTo((Quantity)right);
          return nullableCompareTo == null ? null : nullableCompareTo <= 0;
      }

      else if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
          Integer i = ((BaseTemporal) left).compare((BaseTemporal) right, false);
          return i == null ? null : i <= 0;
      }

      else if (left instanceof String && right instanceof String) {
          return ((String) left).compareTo((String) right) <= 0;
      }

      // Uncertainty comparisons for difference/duration between
      else if ((left instanceof Interval && right instanceof Integer)
                    || (left instanceof Integer && right instanceof Interval))
      {
          return LessEvaluator.less(left, right, context);
      }

      throw new InvalidOperatorArgument(
          "LessOrEqual(Integer, Integer), LessOrEqual(Long, Long), LessOrEqual(Decimal, Decimal), LessOrEqual(Quantity, Quantity), LessOrEqual(Date, Date), LessOrEqual(DateTime, DateTime), LessOrEqual(Time, Time) or LessOrEqual(String, String)",
          String.format("LessOrEqual(%s, %s)", left.getClass().getSimpleName(), right.getClass().getSimpleName())
      );
  }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);

        return lessOrEqual(left, right, context);
    }
}
