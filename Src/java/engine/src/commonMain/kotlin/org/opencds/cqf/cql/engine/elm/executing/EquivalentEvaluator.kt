package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.math.min
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.elm.executing.MultiplyEvaluator.multiply
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.runtime.Quantity.Companion.unitsEquivalent
import org.opencds.cqf.cql.engine.util.javaClassName

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
@Suppress("ReturnCount", "TooManyFunctions", "LongMethod")
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

        // Cases in which Java classes may differ

        if (left is Iterable<*> && right is Iterable<*>) {
            return listEquivalent(left, right, state)
        }

        if (left is Interval && right is Int) {
            return intervalIntegerEquivalent(left, right, state)
        }

        if (right is Interval && left is Int) {
            return intervalIntegerEquivalent(right, left, state)
        }

        // Return false early if Java classes don't match (platform dependence)

        if (left::class != right::class) {
            return false
        }

        // The rest of the cases

        if (left is Boolean || left is Int || left is Long) {
            return left == right
        }

        if (left is BigDecimal && right is BigDecimal) {
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

        if (left is String && right is String) {
            return left.equals(right, ignoreCase = true)
        }

        if (left is Quantity && right is Quantity) {
            return quantitiesEquivalent(left, right, state)
        }

        if (left is Ratio && right is Ratio) {
            return ratiosEquivalent(left, right, state)
        }

        if (left is BaseTemporal && right is BaseTemporal) {
            return baseTemporalsEquivalent(left, right)
        }

        if (left is Code && right is Code) {
            return codesEquivalent(left, right)
        }

        if (left is CodeSystem && right is CodeSystem) {
            return codeSystemsEquivalent(left, right)
        }

        if (left is ValueSet && right is ValueSet) {
            return valueSetsEquivalent(left, right)
        }

        if (left is Concept && right is Concept) {
            return conceptsEquivalent(left, right)
        }

        if (left is Interval && right is Interval) {
            return intervalsEquivalent(left, right, state)
        }

        if (left is Tuple && right is Tuple) {
            return tuplesEquivalent(left, right, state)
        }

        // Fallback to data provider's `objectEquivalent()`

        if (state != null) {
            return state.environment.objectEquivalent(left, right)
        }

        throw InvalidOperatorArgument(
            "Equivalent(${left.javaClassName}, ${right.javaClassName}) requires Context and context was null"
        )
    }

    fun quantitiesEquivalent(left: Quantity, right: Quantity, state: State?): Boolean? {
        // Try the "simple" rule (equality of alternate spellings for "week" or "month").
        val simpleResult =
            if (unitsEquivalent(left.unit, right.unit)) equivalent(left.value, right.value)
            else false
        if (simpleResult != false) {
            return simpleResult // true or null
        } else {
            // The simple rule indicated that the units are not comparable, try to convert the value
            // of right Quantity to the unit of left Quantity and check for equivalence again if the
            // conversion is possible.
            val fullResult =
                computeWithConvertedUnits(
                    left,
                    right,
                    { _, leftValue, rightValue -> equivalent(leftValue, rightValue) },
                    state!!,
                )
            return fullResult ?: false
        }
    }

    /**
     * For ratios, equivalent means that the numerator and denominator represent the same ratio
     * (e.g. 1:100 ~ 10:1000).
     */
    fun ratiosEquivalent(left: Ratio, right: Ratio, state: State?): Boolean? {
        return equivalent(
            multiply(left.numerator, right.denominator, state),
            multiply(right.numerator, left.denominator, state),
            state,
        )
    }

    fun baseTemporalsEquivalent(left: BaseTemporal, right: BaseTemporal): Boolean {
        return left.compare(right, false) == 0
    }

    fun codesEquivalent(left: Code, right: Code): Boolean {
        return equivalent(left.code, right.code) == true &&
            equivalent(left.system, right.system) == true
    }

    fun codeSystemsEquivalent(left: CodeSystem, right: CodeSystem): Boolean? {
        return vocabulariesEquivalent(left, right)
    }

    fun valueSetsEquivalent(left: ValueSet, right: ValueSet): Boolean {
        val equivalent =
            vocabulariesEquivalent(left, right) == true &&
                left.codeSystems.size == right.codeSystems.size
        if (equivalent) {
            for (cs in left.codeSystems) {
                val otherC = right.getCodeSystem(cs.id)
                if (otherC == null) {
                    return false
                }
            }
        }
        return equivalent
    }

    fun vocabulariesEquivalent(left: Vocabulary, right: Vocabulary): Boolean? {
        return equivalent(left.version, right.version)
    }

    fun conceptsEquivalent(left: Concept, right: Concept): Boolean {
        if (left.codes == null || right.codes == null) {
            return false
        }

        for (code in left.codes) {
            for (otherCode in right.codes!!) {
                if (equivalent(code, otherCode) == true) {
                    return true
                }
            }
        }

        return false
    }

    fun intervalsEquivalent(left: Interval, right: Interval, state: State?): Boolean {
        return equivalent(left.start, right.start, state) == true &&
            equivalent(left.end, right.end, state) == true
    }

    fun intervalIntegerEquivalent(interval: Interval, integer: Int, state: State?): Boolean {
        return intervalsEquivalent(interval, Interval(integer, true, integer, true, state), state)
    }

    fun listEquivalent(left: Iterable<*>, right: Iterable<*>, state: State?): Boolean {
        val leftIterator = left.iterator()
        val rightIterator = right.iterator()

        while (leftIterator.hasNext()) {
            val leftObject = leftIterator.next()
            if (rightIterator.hasNext()) {
                val rightObject = rightIterator.next()
                val elementEquivalent = equivalent(leftObject, rightObject, state)
                if (!elementEquivalent!!) {
                    return false
                }
            } else {
                return false
            }
        }

        return !rightIterator.hasNext()
    }

    fun tuplesEquivalent(left: Tuple, right: Tuple, state: State?): Boolean {
        if (left.elements.size != right.elements.size) {
            return false
        }

        for (key in right.elements.keys) {
            if (left.elements.containsKey(key)) {
                val areKeyValsSame =
                    equivalent(right.elements[key], left.elements[key], state) == true
                if (!areKeyValsSame) {
                    return false
                }
            } else {
                return false
            }
        }
        return true
    }
}
