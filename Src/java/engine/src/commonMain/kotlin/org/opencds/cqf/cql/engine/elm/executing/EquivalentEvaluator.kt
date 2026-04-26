package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.math.min
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.elm.executing.MultiplyEvaluator.multiply
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Quantity.Companion.unitsEquivalent
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.Vocabulary
import org.opencds.cqf.cql.engine.runtime.computeWithConvertedUnits
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlString

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
    fun equivalent(left: Value?, right: Value?, state: State? = null): Boolean {
        if (left == null && right == null) {
            return Boolean.TRUE
        }

        if (left == null || right == null) {
            return Boolean.FALSE
        }

        // Cases in which Kotlin classes may differ

        if (left is Interval && right is Integer) {
            return intervalIntegerEquivalent(left, right, state)
        }

        if (right is Interval && left is Integer) {
            return intervalIntegerEquivalent(right, left, state)
        }

        // Return false early if Kotlin classes don't match

        if (left::class != right::class) {
            return Boolean.FALSE
        }

        // The rest of the cases

        if (left is Boolean || left is Integer || left is Long) {
            return (left == right).toCqlBoolean()
        }

        if (left is Decimal && right is Decimal) {
            val leftDecimal = DecimalHelper.verifyPrecision(left.value, 0)
            val rightDecimal = DecimalHelper.verifyPrecision(right.value, 0)
            val minScale = min(leftDecimal.scale(), rightDecimal.scale())
            if (minScale >= 0) {
                return (leftDecimal
                        .setScale(minScale, RoundingMode.HALF_UP)
                        .compareTo(rightDecimal.setScale(minScale, RoundingMode.HALF_UP)) == 0)
                    .toCqlBoolean()
            }
            return (leftDecimal.compareTo(rightDecimal) == 0).toCqlBoolean()
        }

        if (left is String && right is String) {
            return left.value.equals(right.value, ignoreCase = true).toCqlBoolean()
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

        if (left is List && right is List) {
            return listEquivalent(left, right, state)
        }

        if (left is Interval && right is Interval) {
            return intervalsEquivalent(left, right, state)
        }

        if (left is Tuple && right is Tuple) {
            return structuredValueElementsEquivalent(left.elements, right.elements, state)
        }

        if (left is ClassInstance && right is ClassInstance) {
            if (left.type == right.type) {
                return structuredValueElementsEquivalent(left.elements, right.elements, state)
            }
            return Boolean.FALSE
        }

        return Boolean.FALSE
    }

    fun quantitiesEquivalent(left: Quantity, right: Quantity, state: State?): Boolean {
        // Try the "simple" rule (equality of alternate spellings for "week" or "month").
        val simpleResult =
            if (unitsEquivalent(left.unit, right.unit))
                equivalent(left.value?.toCqlDecimal(), right.value?.toCqlDecimal()).value
            else false
        if (simpleResult) {
            return Boolean.TRUE
        } else {
            // The simple rule indicated that the units are not comparable, try to convert the value
            // of right Quantity to the unit of left Quantity and check for equivalence again if the
            // conversion is possible.
            val fullResult =
                computeWithConvertedUnits(
                    left,
                    right,
                    { _, leftValue, rightValue ->
                        equivalent(leftValue.toCqlDecimal(), rightValue.toCqlDecimal()).value
                    },
                    state!!,
                )
            return fullResult?.toCqlBoolean() ?: Boolean.FALSE
        }
    }

    /**
     * For ratios, equivalent means that the numerator and denominator represent the same ratio
     * (e.g. 1:100 ~ 10:1000).
     */
    fun ratiosEquivalent(left: Ratio, right: Ratio, state: State?): Boolean {
        return equivalent(
            multiply(left.numerator, right.denominator, state),
            multiply(right.numerator, left.denominator, state),
            state,
        )
    }

    fun baseTemporalsEquivalent(left: BaseTemporal, right: BaseTemporal): Boolean {
        return (left.compare(right, false) == 0).toCqlBoolean()
    }

    fun codesEquivalent(left: Code, right: Code): Boolean {
        return AndEvaluator.and(
            equivalent(left.code?.toCqlString(), right.code?.toCqlString()),
            equivalent(left.system?.toCqlString(), right.system?.toCqlString()),
        )
    }

    fun codeSystemsEquivalent(left: CodeSystem, right: CodeSystem): Boolean {
        return vocabulariesEquivalent(left, right)
    }

    fun valueSetsEquivalent(left: ValueSet, right: ValueSet): Boolean {
        val equivalent =
            vocabulariesEquivalent(left, right).value == true &&
                left.codeSystems.size == right.codeSystems.size
        if (equivalent) {
            for (cs in left.codeSystems) {
                val otherC = right.getCodeSystem(cs.id)
                if (otherC == null) {
                    return Boolean.FALSE
                }
            }
        }
        return equivalent.toCqlBoolean()
    }

    fun vocabulariesEquivalent(left: Vocabulary, right: Vocabulary): Boolean {
        return equivalent(left.version?.toCqlString(), right.version?.toCqlString())
    }

    fun conceptsEquivalent(left: Concept, right: Concept): Boolean {
        if (left.codes == null || right.codes == null) {
            return Boolean.FALSE
        }

        for (code in left.codes) {
            for (otherCode in right.codes!!) {
                if (equivalent(code, otherCode).value) {
                    return Boolean.TRUE
                }
            }
        }

        return Boolean.FALSE
    }

    fun intervalsEquivalent(left: Interval, right: Interval, state: State?): Boolean {
        return AndEvaluator.and(
            equivalent(left.start, right.start, state),
            equivalent(left.end, right.end, state),
        )
    }

    fun intervalIntegerEquivalent(interval: Interval, integer: Integer, state: State?): Boolean {
        return intervalsEquivalent(interval, Interval(integer, true, integer, true, state), state)
    }

    fun listEquivalent(left: List, right: List, state: State?): Boolean {
        val leftIterator = left.iterator()
        val rightIterator = right.iterator()

        while (leftIterator.hasNext()) {
            val leftObject = leftIterator.next()
            if (rightIterator.hasNext()) {
                val rightObject = rightIterator.next()
                val elementEquivalent = equivalent(leftObject, rightObject, state)
                if (!elementEquivalent.value) {
                    return Boolean.FALSE
                }
            } else {
                return Boolean.FALSE
            }
        }

        return (!rightIterator.hasNext()).toCqlBoolean()
    }

    fun structuredValueElementsEquivalent(
        left: Map<kotlin.String, Value?>,
        right: Map<kotlin.String, Value?>,
        state: State?,
    ): Boolean {
        if (left.size != right.size) {
            return Boolean.FALSE
        }

        for (key in right.keys) {
            if (left.containsKey(key)) {
                val areKeyValsSame = equivalent(right[key], left[key], state)
                if (!areKeyValsSame.value) {
                    return Boolean.FALSE
                }
            } else {
                return Boolean.FALSE
            }
        }
        return Boolean.TRUE
    }
}
