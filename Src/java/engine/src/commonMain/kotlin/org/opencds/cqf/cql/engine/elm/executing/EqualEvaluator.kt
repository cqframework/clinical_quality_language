package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.elm.executing.OrEvaluator.or
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Quantity.Companion.unitsEqual
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.Vocabulary
import org.opencds.cqf.cql.engine.runtime.computeWithConvertedUnits
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

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
@Suppress("ReturnCount", "TooManyFunctions", "CyclomaticComplexMethod", "ComplexCondition")
object EqualEvaluator {
    @JvmStatic
    @JvmOverloads
    fun equal(left: CqlType?, right: CqlType?, state: State? = null): Boolean? {

        // If either argument is null, the result is null

        if (left == null || right == null) {
            return null
        }

        // Cases in which Kotlin classes may differ

        if (left is Interval && right is Integer) {
            return intervalIntegerEqual(left, right, state)
        }

        if (right is Interval && left is Integer) {
            return intervalIntegerEqual(right, left, state)
        }

        // Return false early if Kotlin classes don't match

        if (left::class != right::class) {
            return Boolean.FALSE
        }

        // The rest of the cases

        if (left is Boolean || left is Integer || left is Long || left is String) {
            return (left == right).toCqlBoolean()
        }

        if (left is Decimal && right is Decimal) {
            return (left.value.compareTo(right.value) == 0).toCqlBoolean()
        }

        if (left is Quantity && right is Quantity) {
            return quantitiesEqual(left, right, state)
        }

        if (left is Ratio && right is Ratio) {
            return ratiosEqual(left, right, state)
        }

        if (left is BaseTemporal && right is BaseTemporal) {
            return baseTemporalsEqual(left, right)
        }

        if (left is Code && right is Code) {
            return codesEqual(left, right)
        }

        if (left is CodeSystem && right is CodeSystem) {
            return codeSystemsEqual(left, right)
        }

        if (left is ValueSet && right is ValueSet) {
            return valueSetsEqual(left, right)
        }

        if (left is Concept && right is Concept) {
            return conceptsEqual(left, right)
        }

        if (left is List && right is List) {
            return listsEqual(left, right, state)
        }

        if (left is Interval && right is Interval) {
            return intervalsEqual(left, right, state)
        }

        if (left is Tuple && right is Tuple) {
            return structuredValueElementsEqual(left.elements, right.elements, state)
        }

        if (left is ClassInstance && right is ClassInstance) {
            if (left.type == right.type) {
                return structuredValueElementsEqual(left.elements, right.elements, state)
            }
            return Boolean.FALSE
        }

        return Boolean.FALSE
    }

    fun quantitiesEqual(left: Quantity, right: Quantity, state: State?): Boolean? {
        // Try the "simple" rule (equality of alternate spellings for "week" or "month")
        if (unitsEqual(left.unit, right.unit)) {
            return equal(left.value?.toCqlDecimal(), right.value?.toCqlDecimal())
        }

        // The simple rule indicated that the units are not comparable, try to convert the value of
        // right Quantity to the unit of left Quantity and check for equality again if the
        // conversion is possible.
        return computeWithConvertedUnits(
            left,
            right,
            { _, leftValue, rightValue ->
                equal(leftValue.toCqlDecimal(), rightValue.toCqlDecimal())
            },
            state,
        )
    }

    fun ratiosEqual(left: Ratio, right: Ratio, state: State?): Boolean {
        return (equal(left.numerator, right.numerator, state)?.value == true &&
                equal(left.denominator, right.denominator, state)?.value == true)
            .toCqlBoolean()
    }

    fun baseTemporalsEqual(left: BaseTemporal, right: BaseTemporal): Boolean? {
        val comparison = left.compare(right, false)
        return (if (comparison == null) null else comparison == 0)?.toCqlBoolean()
    }

    fun codesEqual(left: Code, right: Code): Boolean? {
        var codeIsEqual = equal(left.code?.toCqlString(), right.code?.toCqlString())?.value
        var systemIsEqual = equal(left.system?.toCqlString(), right.system?.toCqlString())?.value
        var versionIsEqual = equal(left.version?.toCqlString(), right.version?.toCqlString())?.value
        var displayIsEqual = equal(left.display?.toCqlString(), right.display?.toCqlString())?.value
        if (codeIsEqual == null && left.code == null && right.code == null) {
            codeIsEqual = true
        }
        if (systemIsEqual == null && left.system == null && right.system == null) {
            systemIsEqual = true
        }
        if (versionIsEqual == null && left.version == null && right.version == null) {
            versionIsEqual = true
        }
        if (displayIsEqual == null && left.display == null && right.display == null) {
            displayIsEqual = true
        }
        return (if (
                codeIsEqual == null ||
                    systemIsEqual == null ||
                    versionIsEqual == null ||
                    displayIsEqual == null
            )
                null
            else codeIsEqual && systemIsEqual && versionIsEqual && displayIsEqual)
            ?.toCqlBoolean()
    }

    fun codeSystemsEqual(left: CodeSystem, right: CodeSystem): Boolean? {
        return vocabulariesEqual(left, right)
    }

    fun valueSetsEqual(left: ValueSet, right: ValueSet): Boolean {
        val equal =
            vocabulariesEqual(left, right)?.value == true &&
                left.codeSystems.size == right.codeSystems.size
        if (equal) {
            for (cs in left.codeSystems) {
                val otherC = right.getCodeSystem(cs.id, cs.version)
                if (otherC == null) {
                    return Boolean.FALSE
                }
            }
        }
        return equal.toCqlBoolean()
    }

    fun vocabulariesEqual(left: Vocabulary, right: Vocabulary): Boolean? {
        return AndEvaluator.and(
            or(
                (left.id == null && right.id == null).toCqlBoolean(),
                equal(left.id?.toCqlString(), right.id?.toCqlString()),
            ),
            or(
                (left.version == null && right.version == null).toCqlBoolean(),
                equal(left.version?.toCqlString(), right.version?.toCqlString()),
            ),
        )
    }

    fun conceptsEqual(left: Concept, right: Concept): Boolean? {
        val codesAreEqual = equal(left.codes?.toCqlList(), right.codes?.toCqlList())?.value
        var displayIsEqual = equal(left.display?.toCqlString(), right.display?.toCqlString())?.value
        if (displayIsEqual == null && left.display == null && right.display == null) {
            displayIsEqual = true
        }
        return (if (codesAreEqual == null || displayIsEqual == null) null
            else codesAreEqual && displayIsEqual)
            ?.toCqlBoolean()
    }

    fun intervalsEqual(left: Interval, right: Interval, state: State?): Boolean? {
        if (left.isUncertain) {
            if (IntersectEvaluator.intersect(left, right, state) != null) {
                return null
            }
        }

        return AndEvaluator.and(
            equal(left.start, right.start, state),
            equal(left.end, right.end, state),
        )
    }

    fun intervalIntegerEqual(interval: Interval, integer: Integer, state: State?): Boolean? {
        return intervalsEqual(interval, Interval(integer, true, integer, true, state), state)
    }

    fun listsEqual(left: List, right: List, state: State?): Boolean? {
        val leftIterator = left.iterator()
        val rightIterator = right.iterator()

        if (!leftIterator.hasNext() || !rightIterator.hasNext()) {
            return null
        }

        while (leftIterator.hasNext()) {
            val leftObject = leftIterator.next()
            if (rightIterator.hasNext()) {
                val rightObject = rightIterator.next()
                if (leftObject == null && rightObject == null) {
                    continue
                }
                val elementEquals = equal(leftObject, rightObject, state)
                if (elementEquals == null || !elementEquals.value) {
                    return elementEquals
                }
            } else if (leftObject == null) {
                return null
            } else {
                return Boolean.FALSE
            }
        }

        if (rightIterator.hasNext()) {
            return if (rightIterator.next() == null) null else Boolean.FALSE
        }

        return Boolean.TRUE
    }

    fun structuredValueElementsEqual(
        left: Map<kotlin.String, CqlType?>,
        right: Map<kotlin.String, CqlType?>,
        state: State?,
    ): Boolean? {
        if (left.size != right.size) {
            return Boolean.FALSE
        }

        for (key in right.keys) {
            if (left.containsKey(key)) {
                if (right[key] == null && left[key] == null) {
                    continue
                }
                val equal = equal(right[key], left[key], state)
                if (equal == null) {
                    return null
                } else if (!equal.value) {
                    return Boolean.FALSE
                }
            } else {
                return Boolean.FALSE
            }
        }

        return Boolean.TRUE
    }
}
