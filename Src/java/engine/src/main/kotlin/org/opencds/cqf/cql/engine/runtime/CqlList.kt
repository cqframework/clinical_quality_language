package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Expression
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.exception.InvalidComparison
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable

class CqlList {
    private var state: State? = null
    private var alias: String? = null
    private var expression: Expression? = null
    private var visitor: ElmLibraryVisitor<Any?, State?>? = null
    private var path: String? = null

    constructor()

    constructor(state: State?) {
        this.state = state
    }

    constructor(
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
        alias: String?,
        expression: Expression,
    ) {
        this.state = state
        this.visitor = visitor
        this.alias = alias
        this.expression = expression
    }

    constructor(state: State?, path: String?) {
        this.state = state
        this.path = path
    }

    var valueSort: Comparator<Any?> = Comparator { left, right -> this.compareTo(left, right) }

    var expressionSort: Comparator<Any?> = Comparator { left, right ->
        var leftResult: Any? = null
        try {
            state!!.push(Variable(alias!!).withValue(left))
            leftResult = visitor!!.visitExpression(expression!!, state)
        } finally {
            state!!.pop()
        }

        var rightResult: Any? = null
        try {
            state!!.push(Variable(alias!!).withValue(right))
            rightResult = visitor!!.visitExpression(expression!!, state)
        } finally {
            state!!.pop()
        }

        compareTo(leftResult, rightResult)
    }

    val columnSort: Comparator<Any?> = Comparator { left, right ->
        val leftCol = state!!.environment.resolvePath(left, path!!)
        val rightCol = state!!.environment.resolvePath(right, path!!)

        compareTo(leftCol, rightCol)
    }

    fun compareTo(left: Any?, right: Any?): Int {
        if (left == null && right == null) return 0
        else if (left == null) return -1 else if (right == null) return 1

        // TODO(jmoringe): test is something like
        // ({5 'ml',0.001 'l',0.02 'dl',3 'ml',4 'ml',6 'ml'}) l sort desc
        if (left is Quantity && right is Quantity) {
            val nullableCompareTo = compareQuantities(left, right, state)
            if (nullableCompareTo != null) {
                return nullableCompareTo
            } else {
                throw InvalidComparison("Quantity $left is not comparable to quantity $right")
            }
        } else {
            try {
                // The exception handling below handles the case where left is not Comparable
                @Suppress("UNCHECKED_CAST")
                return (left as Comparable<Any?>).compareTo(right)
            } catch (_: ClassCastException) {
                throw InvalidComparison("Type ${left.javaClass.name} is not comparable")
            }
        }
    }

    companion object {
        fun equivalent(left: Iterable<*>, right: Iterable<*>, state: State?): Boolean {
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

        fun equal(left: Iterable<*>, right: Iterable<*>, state: State?): Boolean? {
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

        fun <T> toList(iterable: Iterable<T>, includeNullElements: Boolean): MutableList<T> {
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
