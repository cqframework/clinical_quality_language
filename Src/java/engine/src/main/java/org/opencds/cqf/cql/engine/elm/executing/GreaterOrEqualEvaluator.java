package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;

import java.math.BigDecimal;

/*
>=(left Integer, right Integer) Boolean
>=(left Long, right Long) Boolean
>=(left Decimal, right Decimal) Boolean
>=(left Quantity, right Quantity) Boolean
>=(left DateTime, right DateTime) Boolean
>=(left Time, right Time) Boolean
>=(left String, right String) Boolean

The greater or equal (>=) operator returns true if the first argument is greater than or equal to the second argument.
For comparisons involving quantities, the dimensions of each quantity must be the same, but not necessarily the unit.
  For example, units of 'cm' and 'm' are comparable, but units of 'cm2' and  'cm' are not.
For comparisons involving date/time or time values with imprecision, note that the result of the comparison may be null,
  depending on whether the values involved are specified to the level of precision used for the comparison.
If either argument is null, the result is null.
*/

public class GreaterOrEqualEvaluator {

  public static Boolean greaterOrEqual(Object left, Object right, State state) {

    if (left == null || right == null) {
        return null;
    }

      if (left instanceof Integer && right instanceof Integer) {
          return ((Integer) left).compareTo((Integer) right) >= 0;
      }

      if (left instanceof Long && right instanceof Long) {
          return ((Long) left).compareTo((Long) right) >= 0;
      }

      else if (left instanceof BigDecimal && right instanceof BigDecimal) {
          return ((BigDecimal) left).compareTo((BigDecimal) right) >= 0;
      }

      else if (left instanceof Quantity && right instanceof Quantity) {
          if (((Quantity) left).getValue() == null || ((Quantity) right).getValue() == null) {
              return null;
          }
          Integer nullableCompareTo = ((Quantity)left).nullableCompareTo((Quantity)right);
          return nullableCompareTo == null ? null : nullableCompareTo >= 0;
      }

      else if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
          Integer i = ((BaseTemporal) left).compare((BaseTemporal) right, false);
          return i == null ? null : i >= 0;
      }

      else if (left instanceof String && right instanceof String) {
          return ((String) left).compareTo((String) right) >= 0;
      }

      // Uncertainty comparisons for difference/duration between
      else if ((left instanceof Interval && right instanceof Integer)
                    || (left instanceof Integer && right instanceof Interval))
      {
          return GreaterEvaluator.greater(left, right, state);
      }

      throw new InvalidOperatorArgument(
          "GreaterOrEqual(Integer, Integer), GreaterOrEqual(Long, Long), GreaterOrEqual(Decimal, Decimal), GreaterOrEqual(Quantity, Quantity), GreaterOrEqual(Date, Date), GreaterOrEqual(DateTime, DateTime), GreaterOrEqual(Time, Time) or GreaterOrEqual(String, String)",
          String.format("Cannot perform greater than or equal operator on types %s and %s",
              left.getClass().getSimpleName(), right.getClass().getSimpleName()));
  }
}
