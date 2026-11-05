package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.exception.InvalidComparison
import org.opencds.cqf.cql.engine.execution.State

object SortHelper {
    fun compare(left: Value?, right: Value?, state: State?): Int {
        if (left == null && right == null) return 0
        else if (left == null) return -1 else if (right == null) return 1

        if (left is Boolean && right is Boolean) {
            return left.value.compareTo(right.value)
        }

        if (left is Integer && right is Integer) {
            return left.value.compareTo(right.value)
        }

        if (left is Long && right is Long) {
            return left.value.compareTo(right.value)
        }

        if (left is Decimal && right is Decimal) {
            return left.value.compareTo(right.value)
        }

        if (left is String && right is String) {
            return left.value.compareTo(right.value)
        }

        if (left is BaseTemporal && right is BaseTemporal) {
            return left.compareTo(right)
        }

        // TODO(jmoringe): test is something like
        // ({5 'ml',0.001 'l',0.02 'dl',3 'ml',4 'ml',6 'ml'}) l sort desc
        if (left is Quantity && right is Quantity) {
            val nullableCompareTo = compareQuantities(left, right, state)
            if (nullableCompareTo != null) {
                return nullableCompareTo
            } else {
                throw InvalidComparison("Quantity $left is not comparable to quantity $right")
            }
        }

        if (left is Interval && right is Interval) {

            if (compare(left.start, right.start, state) == 0) {
                return compare(left.end, right.end, state)
            }
            return compare(left.start, right.start, state)
        }

        throw InvalidComparison(
            "Values ${left.typeAsString} and ${right.typeAsString} are not comparable"
        )
    }
}
