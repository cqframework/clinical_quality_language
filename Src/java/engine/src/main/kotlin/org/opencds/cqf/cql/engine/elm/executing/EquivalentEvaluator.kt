package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.min
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlList
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Value

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
object EquivalentEvaluator {
    @JvmStatic
    @JvmOverloads
    fun equivalent(left: Any?, right: Any?, state: State? = null): Boolean? {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        if (left is Interval && right is Int) {
            return left.equivalent(right)
        }

        if (right is Interval && left is Int) {
            return right.equivalent(left)
        }

        if (left.javaClass != right.javaClass) {
            return false
        } else if (left is Boolean || left is Int) {
            return left == right
        } else if (left is BigDecimal && right is BigDecimal) {
            val leftDecimal = Value.verifyPrecision(left, 0)
            val rightDecimal = Value.verifyPrecision(right, 0)
            val minScale = min(leftDecimal.scale(), rightDecimal.scale())
            if (minScale >= 0) {
                return (leftDecimal
                    .setScale(minScale, RoundingMode.HALF_UP)
                    .compareTo(rightDecimal.setScale(minScale, RoundingMode.HALF_UP)) == 0)
            }
            return leftDecimal.compareTo(rightDecimal) == 0
        }

        if (left is Iterable<*>) {
            return CqlList.equivalent(left, right as Iterable<*>, state)
        } else if (left is CqlType) {
            return left.equivalent(right)
        } else if (left is String) {
            return left.equals(right as String, ignoreCase = true)
        }

        if (state != null) {
            return state.environment.objectEquivalent(left, right)
        }

        throw InvalidOperatorArgument(
            "Equivalent(${left.javaClass.name}, ${right.javaClass.name}) requires Context and context was null"
        )
    }
}
