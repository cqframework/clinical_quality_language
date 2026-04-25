package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Expression
import org.opencds.cqf.cql.engine.elm.executing.PropertyEvaluator
import org.opencds.cqf.cql.engine.exception.InvalidComparison
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable

class CqlList {
    private var state: State? = null
    private var alias: kotlin.String? = null
    private var expression: Expression? = null
    private var visitor: ElmLibraryVisitor<CqlType?, State?>? = null
    private var path: kotlin.String? = null

    constructor()

    constructor(state: State?) {
        this.state = state
    }

    constructor(
        state: State?,
        visitor: ElmLibraryVisitor<CqlType?, State?>,
        alias: kotlin.String?,
        expression: Expression,
    ) {
        this.state = state
        this.visitor = visitor
        this.alias = alias
        this.expression = expression
    }

    constructor(state: State?, path: kotlin.String?) {
        this.state = state
        this.path = path
    }

    var valueSort: Comparator<CqlType?> = Comparator { left, right -> this.compareTo(left, right) }

    var expressionSort: Comparator<CqlType?> = Comparator { left, right ->
        var leftResult: CqlType? = null
        try {
            state!!.push(Variable(alias!!).withValue(left))
            leftResult = visitor!!.visitExpression(expression!!, state)
        } finally {
            state!!.pop()
        }

        var rightResult: CqlType? = null
        try {
            state!!.push(Variable(alias!!).withValue(right))
            rightResult = visitor!!.visitExpression(expression!!, state)
        } finally {
            state!!.pop()
        }

        compareTo(leftResult, rightResult)
    }

    val columnSort: Comparator<CqlType?> = Comparator { left, right ->
        val leftCol = PropertyEvaluator.resolvePath(left, path!!)
        val rightCol = PropertyEvaluator.resolvePath(right, path!!)

        compareTo(leftCol, rightCol)
    }

    fun compareTo(left: CqlType?, right: CqlType?): Int {
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
            return left.compareTo(right, state)
        }

        throw InvalidComparison(
            "Values ${left.typeAsString} and ${right.typeAsString} are not comparable"
        )
    }

    companion object {
        fun <T> toList(iterable: Iterable<T>, includeNullElements: kotlin.Boolean): MutableList<T> {
            val ret = mutableListOf<T>()
            for (element in iterable) {
                if (element != null || includeNullElements) {
                    ret.add(element)
                }
            }
            return ret
        }
    }
}
