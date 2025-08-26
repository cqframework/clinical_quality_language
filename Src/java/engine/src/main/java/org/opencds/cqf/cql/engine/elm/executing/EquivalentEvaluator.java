package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.*;

/*
https://cql.hl7.org/09-b-cqlreference.html#equivalent

*** NOTES FOR CLINICAL OPERATORS ***
~(left Code, right Code) Boolean

The ~ operator for Code values returns true if the code, system, and version elements are equivalent.
  The display element is ignored for the purposes of determining Code equivalence.
For Concept values, equivalence is defined as a non-empty intersection of the codes in each Concept.
  The display element is ignored for the purposes of determining Concept equivalence.
Note that this operator will always return true or false, even if either or both of its arguments are null,
  or contain null components.
Note carefully that this notion of equivalence is not the same as the notion of equivalence used in terminology:
  "these codes represent the same concept." CQL specifically avoids defining terminological equivalence.
    The notion of equivalence defined here is used to provide consistent and intuitive semantics when dealing with
      missing information in membership contexts.

*** NOTES FOR INTERVAL ***
~(left Interval<T>, right Interval<T>) Boolean

The ~ operator for intervals returns true if and only if the intervals are over the same point type,
  and the starting and ending points of the intervals as determined by the Start and End operators are equivalent.

*** NOTES FOR LIST ***
~(left List<T>, right List<T>) Boolean

The ~ operator for lists returns true if and only if the lists contain elements of the same type, have the same number of elements,
  and for each element in the lists, in order, the elements are equivalent.

*** NOTES FOR DECIMAL ***
For decimals, equivalent means the values are the same with the comparison done on values rounded to the precision of the
least precise operand; trailing zeroes after the decimal are ignored in determining precision for equivalent comparison.
*/

public class EquivalentEvaluator {

    public static Boolean equivalent(Object left, Object right, State state) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        if (left instanceof Interval && right instanceof Integer) {
            return ((Interval) left).equivalent(right);
        }

        if (right instanceof Interval && left instanceof Integer) {
            return ((Interval) right).equivalent(left);
        }

        if (!left.getClass().equals(right.getClass())) {
            return false;
        } else if (left instanceof Boolean || left instanceof Integer) {
            return left.equals(right);
        } else if (left instanceof BigDecimal && right instanceof BigDecimal) {
            BigDecimal leftDecimal = Value.verifyPrecision((BigDecimal) left, 0);
            BigDecimal rightDecimal = Value.verifyPrecision((BigDecimal) right, 0);
            int minScale = Math.min(leftDecimal.scale(), rightDecimal.scale());
            if (minScale >= 0) {
                return leftDecimal
                                .setScale(minScale, RoundingMode.HALF_UP)
                                .compareTo(rightDecimal.setScale(minScale, RoundingMode.HALF_UP))
                        == 0;
            }
            return leftDecimal.compareTo(rightDecimal) == 0;
        } else if (left instanceof Quantity leftQuantity && right instanceof Quantity rightQuantity) {
            // Try the Quantity.equivalent method which implements "simple" rules such as the equality of alternate
            // spellings for "week" or "month".
            final var simpleResult = leftQuantity.equivalent(rightQuantity);
            if (!Objects.equals(simpleResult, false)) {
                return simpleResult; // true or null
            } else {
                // The simple method indicated that the units are not comparable, try to convert the value of
                // rightQuantity to the unit of leftQuantity and check for equivalence again if the conversion is
                // possible.
                final var fullResult = UnitConversionHelper.computeWithConvertedUnits(
                        leftQuantity,
                        rightQuantity,
                        (commonUnit, leftValue, rightValue) -> EquivalentEvaluator.equivalent(leftValue, rightValue),
                        state);
                return fullResult != null ? fullResult : false;
            }
        } else if (left instanceof Ratio leftRatio && right instanceof Ratio rightRatio) {
            return leftRatio.fullEquivalent(rightRatio, state);
        } else if (left instanceof Iterable<?> leftIterable) {
            return CqlList.equivalent(leftIterable, (Iterable<?>) right, state);
        } else if (left instanceof CqlType leftCqlType) {
            return leftCqlType.equivalent(right);
        } else if (left instanceof String) {
            return ((String) left).equalsIgnoreCase((String) right);
        }

        if (state != null) {
            return state.getEnvironment().objectEquivalent(left, right);
        }

        throw new InvalidOperatorArgument(String.format(
                "Equivalent(%s, %s) requires Context and context was null",
                left.getClass().getName(), right.getClass().getName()));
    }

    public static Boolean equivalent(Object left, Object right) {
        return equivalent(left, right, null);
    }
}
