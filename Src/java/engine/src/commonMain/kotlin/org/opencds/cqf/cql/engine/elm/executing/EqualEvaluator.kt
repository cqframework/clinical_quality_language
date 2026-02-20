package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.elm.executing.OrEvaluator.or
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.runtime.Quantity.Companion.unitsEqual
import org.opencds.cqf.cql.engine.util.javaClassName

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
    fun equal(left: Any?, right: Any?, state: State? = null): Boolean? {

        // If either argument is null, the result is null

        if (left == null || right == null) {
            return null
        }

        // Cases in which Java classes may differ

        if (left is Iterable<*> && right is Iterable<*>) {
            return listsEqual(left, right, state)
        }

        if (left is Interval && right is Int) {
            return intervalIntegerEqual(left, right, state)
        }

        if (right is Interval && left is Int) {
            return intervalIntegerEqual(right, left, state)
        }

        // Return false early if Java classes don't match (platform dependence)

        if (left::class != right::class) {
            return false
        }

        // The rest of the cases

        if (left is Boolean || left is Int || left is Long || left is String) {
            return left == right
        }

        if (left is BigDecimal && right is BigDecimal) {
            return left.compareTo(right) == 0
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

        if (left is Interval && right is Interval) {
            return intervalsEqual(left, right, state)
        }

        if (left is Tuple && right is Tuple) {
            return tuplesEqual(left, right, state)
        }

        // Fallback to data provider's `objectEqual()`

        if (state != null) {
            return state.environment.objectEqual(left, right)
        }

        throw InvalidOperatorArgument(
            "Equal(${left.javaClassName}, ${right.javaClassName}) requires Context and state was null"
        )
    }

    fun quantitiesEqual(left: Quantity, right: Quantity, state: State?): Boolean? {
        // Try the "simple" rule (equality of alternate spellings for "week" or "month")
        if (unitsEqual(left.unit, right.unit)) {
            return equal(left.value, right.value)
        }

        // The simple rule indicated that the units are not comparable, try to convert the value of
        // right Quantity to the unit of left Quantity and check for equality again if the
        // conversion is possible.
        return computeWithConvertedUnits(
            left,
            right,
            { _, leftValue, rightValue -> equal(leftValue, rightValue) },
            state,
        )
    }

    fun ratiosEqual(left: Ratio, right: Ratio, state: State?): Boolean {
        return equal(left.numerator, right.numerator, state) == true &&
            equal(left.denominator, right.denominator, state) == true
    }

    fun baseTemporalsEqual(left: BaseTemporal, right: BaseTemporal): Boolean? {
        val comparison = left.compare(right, false)
        return if (comparison == null) null else comparison == 0
    }

    fun codesEqual(left: Code, right: Code): Boolean? {
        var codeIsEqual = equal(left.code, right.code)
        var systemIsEqual = equal(left.system, right.system)
        var versionIsEqual = equal(left.version, right.version)
        var displayIsEqual = equal(left.display, right.display)
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
        return if (
            codeIsEqual == null ||
                systemIsEqual == null ||
                versionIsEqual == null ||
                displayIsEqual == null
        )
            null
        else codeIsEqual && systemIsEqual && versionIsEqual && displayIsEqual
    }

    fun codeSystemsEqual(left: CodeSystem, right: CodeSystem): Boolean? {
        return vocabulariesEqual(left, right)
    }

    fun valueSetsEqual(left: ValueSet, right: ValueSet): Boolean {
        val equal =
            vocabulariesEqual(left, right) == true &&
                left.codeSystems.size == right.codeSystems.size
        if (equal) {
            for (cs in left.codeSystems) {
                val otherC = right.getCodeSystem(cs.id, cs.version)
                if (otherC == null) {
                    return false
                }
            }
        }
        return equal
    }

    fun vocabulariesEqual(left: Vocabulary, right: Vocabulary): Boolean? {
        return AndEvaluator.and(
            or(left.id == null && right.id == null, equal(left.id, right.id)),
            or(left.version == null && right.version == null, equal(left.version, right.version)),
        )
    }

    fun conceptsEqual(left: Concept, right: Concept): Boolean? {
        val codesAreEqual = equal(left.codes, right.codes)
        var displayIsEqual = equal(left.display, right.display)
        if (displayIsEqual == null && left.display == null && right.display == null) {
            displayIsEqual = true
        }
        return if (codesAreEqual == null || displayIsEqual == null) null
        else codesAreEqual && displayIsEqual
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

    fun intervalIntegerEqual(interval: Interval, integer: Int, state: State?): Boolean? {
        return intervalsEqual(interval, Interval(integer, true, integer, true, state), state)
    }

    fun listsEqual(left: Iterable<*>, right: Iterable<*>, state: State?): Boolean? {
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
                if (elementEquals == null || !elementEquals) {
                    return elementEquals
                }
            } else if (leftObject == null) {
                return null
            } else {
                return false
            }
        }

        if (rightIterator.hasNext()) {
            return if (rightIterator.next() == null) null else false
        }

        return true
    }

    fun tuplesEqual(left: Tuple, right: Tuple, state: State?): Boolean? {
        if (left.elements.size != right.elements.size) {
            return false
        }

        for (key in right.elements.keys) {
            if (left.elements.containsKey(key)) {
                if (right.elements[key] == null && left.elements[key] == null) {
                    continue
                }
                val equal = equal(right.elements[key], left.elements[key], state)
                if (equal == null) {
                    return null
                } else if (!equal) {
                    return false
                }
            } else {
                return false
            }
        }

        return true
    }
}
