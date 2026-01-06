package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.*

/*
*** NOTES FOR CLINICAL OPERATORS ***
=(left Code, right Code) Boolean
=(left Concept, right Concept) Boolean

The equal (=) operator for Codes and Concepts uses tuple equality semantics.
  This means that the operator will return true if and only if the values for each element by name are equal.
If either argument is null, or contains any null components, the result is null.

*** NOTES FOR INTERVAL ***
=(left Interval<T>, right Interval<T>) Boolean

The equal (=) operator for intervals returns true if and only if the intervals are over the same point type,
  and they have the same value for the starting and ending points of the intervals as determined by the Start and End operators.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
=(left List<T>, right List<T>) Boolean

The equal (=) operator for lists returns true if and only if the lists have the same element type,
  and have the same elements by value, in the same order.
If either argument is null, or contains null elements, the result is null.

*/
object EqualEvaluator {
    @JvmStatic
    @JvmOverloads
    fun equal(left: Any?, right: Any?, state: State? = null): Boolean? {
        if (left == null || right == null) {
            return null
        }

        if (left is Iterable<*> && right is Iterable<*>) {
            return CqlList.equal(left, right, state)
        }

        if (left is Interval && right is Int) {
            return left.equal(right)
        }

        if (right is Interval && left is Int) {
            return right.equal(left)
        }

        if (left.javaClass != right.javaClass) {
            return false
        } else if (left is Boolean || left is Int || left is Long || left is String) {
            return left == right
        } else if (left is BigDecimal && right is BigDecimal) {
            return left.compareTo(right) == 0
        } else if (left is Quantity && right is Quantity) {
            // Try the Quantity.equal method which implements "simple" rules such as the equality of
            // alternate
            // spellings for "week" or "month".
            val simpleResult = left.equal(right)
            if (simpleResult != null) {
                return simpleResult // true or false
            } else {
                // The simple method indicated that the units are not comparable, try to convert the
                // value of
                // rightQuantity to the unit of leftQuantity and check for equality again if the
                // conversion is
                // possible.
                return computeWithConvertedUnits(
                    left,
                    right,
                    { _, leftValue, rightValue -> equal(leftValue, rightValue) },
                    state!!,
                )
            }
        } else if (left is CqlType && right is CqlType) {
            return left.equal(right)
        }

        if (state != null) {
            return state.environment.objectEqual(left, right)
        }

        throw InvalidOperatorArgument(
            "Equal(${left.javaClass.name}, ${right.javaClass.name}) requires Context and state was null"
        )
    }
}
