package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*
    There are two overloads of this operator:
        List, T: The type of T must be the same as the element type of the list.
        Interval, T : The type of T must be the same as the point type of the interval.

    For the List, T overload, this operator returns true if the list contains (i.e. includes)
        the element, and it is not the only element in the list, using equality semantics, with
        the exception that null elements are considered equal. The list has more elements than
        just the one searched for (list semantics), and otherwise the result is false
        (FHIR-57746).
        If the first argument is null, the result is false.
    For the Interval, T overload, this operator returns true if the given point is greater than
        the starting point of the interval, and less than the ending point of the interval, as
        determined by the Start and End operators.
        If precision is specified and the point type is a Date, DateTime, or Time type, comparisons
        used in the operation are performed at the specified precision.
        If the first argument is null, the result is false.
        If the second argument is null, the result is null.
*/
object ProperContainsEvaluator {
    fun properContains(left: Value?, right: Value?, state: State?): Boolean? {
        // If the first argument is null, the result is false.

        if (left == null) {
            return Boolean.FALSE
        }

        if (left is Interval) {
            val startProperContains = GreaterEvaluator.greater(right, left.start, state)
            val endProperContains = LessEvaluator.less(right, left.end, state)

            return if (startProperContains == null) null
            else if (endProperContains == null) null
            else (startProperContains.value && endProperContains.value).toCqlBoolean()
        } else if (left is List) {
            // "returns true if the list contains (i.e. includes) the element, and it is not the
            // only element in the list". Following FHIR-57746 (cqframework/cql-tests#118), this
            // has list, not set, semantics: the list has more elements than just the one searched
            // for, so { 'a', 'a' } properly includes 'a'. And membership is true only when the
            // element is known to be in the list, otherwise false, as for the contains operator.
            if (left.count() < 2) {
                return Boolean.FALSE
            }

            return InEvaluator.`in`(right, left, null, state)
        }

        throw InvalidOperatorArgument(
            "ProperContains(List<T>, T) or ProperContains(Interval<T>, T)",
            "ProperContains(${left.typeAsString}, ${right!!.typeAsString})",
        )
    }

    @JvmStatic
    fun properContains(
        left: Value?,
        right: Value?,
        precision: kotlin.String?,
        state: State?,
    ): Boolean? {
        if (left is Interval && right is BaseTemporal) {
            val startProperContains = AfterEvaluator.after(right, left.start, precision, state)
            val endProperContains = BeforeEvaluator.before(right, left.end, precision, state)

            return if (startProperContains == null) null
            else if (endProperContains == null) null
            else (startProperContains.value && endProperContains.value).toCqlBoolean()
        }

        return properContains(left, right, state)
    }
}
