package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
    There are two overloads of this operator:
        List, T: The type of T must be the same as the element type of the list.
        Interval, T : The type of T must be the same as the point type of the interval.

    For the List, T overload, this operator returns true if the given element is in the list,
        and it is not the only element in the list, using equality semantics, with the exception
        that null elements are considered equal.
        If the first argument is null, the result is false.
        If the second argument is null, the result is true if the list contains any null elements
        and at least one other element, and false otherwise.
    For the Interval, T overload, this operator returns true if the given point is greater than
        the starting point of the interval, and less than the ending point of the interval, as
        determined by the Start and End operators.
        If precision is specified and the point type is a Date, DateTime, or Time type, comparisons
        used in the operation are performed at the specified precision.
        If the first argument is null, the result is false.
        If the second argument is null, the result is null.
*/
object ProperContainsEvaluator {
    fun properContains(left: Any?, right: Any?, state: State?): Boolean? {
        // If the first argument is null, the result is false.

        if (left == null) {
            return false
        }

        if (left is Interval) {
            val startProperContains = GreaterEvaluator.greater(right, left.start, state)
            val endProperContains = LessEvaluator.less(right, left.end, state)

            return if (startProperContains == null) null
            else if (endProperContains == null) null else startProperContains && endProperContains
        } else if (left is Iterable<*>) {
            val leftList = left as MutableList<*>

            // The result cannot be true if the list contains fewer than two elements
            if (leftList.size < 2) {
                return false
            }

            if (right == null) {
                // The result is true if the list contains any null elements and at least one other
                // element,
                // and false otherwise

                var listContainsNullElements = false
                var listContainsOtherElements = false

                for (element in leftList) {
                    if (element == null) {
                        listContainsNullElements = true
                        continue
                    }

                    listContainsOtherElements = true
                }

                return listContainsNullElements && listContainsOtherElements
            }

            // Return true if the given element is in the list, and it is not the only element in
            // the list,
            // using equality semantics
            var listContainsGivenElement = false
            var listContainsOtherElements = false
            var listContainsElementsOfUnknownEquality = false

            for (element in leftList) {
                val equalResult = EqualEvaluator.equal(element, right, state)
                if (equalResult == null) {
                    listContainsElementsOfUnknownEquality = true
                    continue
                }
                if (equalResult) {
                    listContainsGivenElement = true
                    continue
                }
                listContainsOtherElements = true
            }

            // The given element is in the list and there are other elements, using equality
            // semantics
            if (listContainsGivenElement && listContainsOtherElements) {
                return true
            }

            // The above is false, but there are elements of unknown equality
            if (listContainsElementsOfUnknownEquality) {
                return null
            }

            return false
        }

        throw InvalidOperatorArgument(
            "ProperContains(List<T>, T) or ProperContains(Interval<T>, T)",
            "ProperContains(${left.javaClassName}, ${right!!.javaClassName})",
        )
    }

    @JvmStatic
    fun properContains(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        if (left is Interval && right is BaseTemporal) {
            val startProperContains = AfterEvaluator.after(right, left.start, precision, state)
            val endProperContains = BeforeEvaluator.before(right, left.end, precision, state)

            return if (startProperContains == null) null
            else if (endProperContains == null) null else startProperContains && endProperContains
        }

        return properContains(left, right, state)
    }
}
