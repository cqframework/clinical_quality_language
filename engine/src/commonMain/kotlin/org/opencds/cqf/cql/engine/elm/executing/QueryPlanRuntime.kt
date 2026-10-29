package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Expression
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value

/**
 * The value of an expression for each element of a source (or for the current row, with a single
 * slot), computed the first time it is needed.
 */
internal class HoistedExpression(val expression: Expression, size: Int) {
    private val values = arrayOfNulls<Value?>(size)
    private val isComputed = BooleanArray(size)

    /** The slot for the current element. */
    var index = 0

    /** True while the expression itself is being evaluated, so that it is not substituted. */
    var isEvaluating = false
        private set

    fun reset() {
        isComputed.fill(false)
    }

    fun value(visitor: ElmLibraryVisitor<Value?, State?>, state: State?): Value? {
        if (!isComputed[index]) {
            isEvaluating = true
            try {
                values[index] = visitor.visitExpression(expression, state)
            } finally {
                isEvaluating = false
            }
            isComputed[index] = true
        }
        return values[index]
    }

    fun isTrue(visitor: ElmLibraryVisitor<Value?, State?>, state: State?): kotlin.Boolean =
        isTrue(value(visitor, state))
}

private fun isTrue(value: Value?): kotlin.Boolean = (value as? Boolean)?.value == true

/** The join key of a null value, which is not equal to anything. */
private object NullKey

/** The join key of a value that cannot be hashed consistently with CQL equality. */
private object UnhashableKey

private data class DecimalKey(val value: kotlin.String)

private data class CodeKey(val code: kotlin.String?, val system: kotlin.String?)

/**
 * A hash key for [value] such that values that are equal (`=`) have equal keys. Distinct values may
 * share a key: candidates found by key are still checked against the full condition.
 */
private fun joinKeyOf(value: Value?): Any =
    when (value) {
        null -> NullKey
        is String -> value.value
        is Integer -> value.value
        is Long -> value.value
        is Boolean -> value.value
        // Equal compares decimals by value, so 1.0 = 1.00
        is Decimal -> DecimalKey(value.value.stripTrailingZeros().toPlainString())
        // Equal compares all elements of codes; these two suffice
        is Code -> CodeKey(value.code, value.system)
        else -> UnhashableKey
    }

/**
 * The positions of the elements of a source by join key, computed in source order and only as far
 * as a lookup needs, so that keys are computed only for elements the naive loop would reach.
 */
private class JoinIndex(private val size: Int, private val keyAt: (Int) -> Any) {
    private var indexed = 0
    private val positions = mutableMapOf<Any, MutableList<Int>>()

    /** Elements with an unhashable key, which may equal any key. */
    private val unhashable = mutableListOf<Int>()

    /**
     * Calls [visit] with the positions, in order, whose elements may equal a value with [key],
     * until it returns true. Returns whether it did.
     */
    fun anyCandidate(key: Any, visit: (Int) -> kotlin.Boolean): kotlin.Boolean {
        var nextKeyed = 0
        var nextUnhashable = 0
        while (true) {
            val keyed = positions[key]?.getOrNull(nextKeyed) ?: Int.MAX_VALUE
            val other = unhashable.getOrNull(nextUnhashable) ?: Int.MAX_VALUE
            val position = minOf(keyed, other)
            when {
                position != Int.MAX_VALUE -> {
                    if (position == keyed) nextKeyed++ else nextUnhashable++
                    if (visit(position)) return true
                }
                indexed < size -> add(indexed++)
                else -> return false
            }
        }
    }

    private fun add(position: Int) {
        when (val key = keyAt(position)) {
            NullKey -> {}
            UnhashableKey -> unhashable.add(position)
            else -> positions.getOrPut(key) { mutableListOf() }.add(position)
        }
    }
}

/**
 * Calls [visit] with the positions among [size] elements that may satisfy `outer = inner` for the
 * [outer] value, in order, until it returns true, using [index] when [outer] can be hashed.
 */
private fun anyCandidate(
    outer: Value?,
    size: Int,
    index: () -> JoinIndex,
    visit: (Int) -> kotlin.Boolean,
): kotlin.Boolean =
    when (val key = joinKeyOf(outer)) {
        // Equal is null, so no element satisfies the condition
        NullKey -> false
        UnhashableKey -> (0 until size).any(visit)
        else -> index().anyCandidate(key, visit)
    }

/** Evaluates a relationship clause following a [RelationshipPlan], for the rows of a query. */
internal class RelationshipRuntime(private val plan: RelationshipPlan, state: State) {
    private var invariantSource: List<Value?>? = null
    private val rowConjuncts = plan.rowConjuncts.map { HoistedExpression(it, 1) }
    private val rowSubexpressions =
        plan.rowSubexpressions.map { HoistedExpression(it, 1).also(state::hoist) }
    private var relatedConjuncts = emptyList<HoistedExpression>()
    private var relatedSubexpressions = emptyList<HoistedExpression>()
    private var joinIndex: JoinIndex? = null

    fun hasSatisfyingData(
        state: State,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): kotlin.Boolean {
        val related = invariantSource ?: evaluateSource(state, visitor)
        rowConjuncts.forEach { it.reset() }
        rowSubexpressions.forEach { it.reset() }
        // The naive loop evaluates the row conjuncts with every related element, so only if
        // there is one; they are the same for all
        return related.isNotEmpty() &&
            rowConjuncts.all { it.isTrue(visitor, state) } &&
            anyRelated(related, state, visitor)
    }

    /** Whether some related element satisfies the pair conjuncts for the current row. */
    private fun anyRelated(
        related: List<Value?>,
        state: State,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): kotlin.Boolean {
        val equiJoin =
            plan.equiJoin
                ?: return related.indices.any { satisfies(it, related[it], state, visitor) }
        return anyCandidate(
            visitor.visitExpression(equiJoin.outer, state),
            related.size,
            {
                joinIndex
                    ?: JoinIndex(related.size) { innerKeyAt(it, related, state, visitor) }
                        .also { joinIndex = it }
            },
        ) {
            satisfies(it, related[it], state, visitor)
        }
    }

    private fun innerKeyAt(
        index: Int,
        related: List<Value?>,
        state: State,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): Any {
        state.push(Variable(plan.relationship.alias!!).withValue(related[index]))
        try {
            relatedSubexpressions.forEach { it.index = index }
            return joinKeyOf(visitor.visitExpression(plan.equiJoin!!.inner, state))
        } finally {
            state.pop()
        }
    }

    private fun satisfies(
        index: Int,
        element: Value?,
        state: State,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): kotlin.Boolean {
        state.push(Variable(plan.relationship.alias!!).withValue(element))
        try {
            relatedConjuncts.forEach { it.index = index }
            relatedSubexpressions.forEach { it.index = index }
            return relatedConjuncts.all { it.isTrue(visitor, state) } &&
                plan.pairConjuncts.all { isTrue(visitor.visitExpression(it, state)) }
        } finally {
            state.pop()
        }
    }

    private fun evaluateSource(
        state: State,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): List<Value?> {
        val related =
            QueryEvaluator.ensureIterable(
                    visitor.visitExpression(plan.relationship.expression!!, state)
                )
                .toList()
        if (plan.isSourceInvariant) {
            invariantSource = related
        }
        relatedSubexpressions.forEach(state::unhoist)
        relatedConjuncts = plan.relatedConjuncts.map { HoistedExpression(it, related.size) }
        relatedSubexpressions =
            plan.relatedSubexpressions.map {
                HoistedExpression(it, related.size).also(state::hoist)
            }
        return related
    }

    fun close(state: State) {
        rowSubexpressions.forEach(state::unhoist)
        relatedSubexpressions.forEach(state::unhoist)
    }
}

/**
 * Evaluates `from A, B, ... where ...` following a [CrossProductPlan], over the given [sources]
 * bound to the given [variables].
 */
internal class CrossProductRuntime(
    private val plan: CrossProductPlan,
    private val sources: List<List<Value?>>,
    private val variables: List<Variable>,
) {
    private val singleSourceConjuncts =
        plan.singleSourceConjuncts.mapIndexed { level, conjuncts ->
            conjuncts.map { HoistedExpression(it, sources[level].size) }
        }

    private val subexpressions =
        plan.singleSourceSubexpressions.mapIndexed { level, expressions ->
            expressions.map { HoistedExpression(it, sources[level].size) }
        }

    private val joinIndexes = arrayOfNulls<JoinIndex>(sources.size)

    /**
     * Calls [emit] for each combination that satisfies the where clause, in the order of the naive
     * nested loop.
     */
    fun evaluate(state: State, visitor: ElmLibraryVisitor<Value?, State?>, emit: () -> Unit) {
        // The naive loop evaluates nothing if a source is empty
        if (
            sources.any { it.isEmpty() } ||
                !plan.invariantConjuncts.all { HoistedExpression(it, 1).isTrue(visitor, state) }
        ) {
            return
        }
        subexpressions.flatten().forEach(state::hoist)
        try {
            descend(0, state, visitor, emit)
        } finally {
            subexpressions.flatten().forEach(state::unhoist)
        }
    }

    private fun descend(
        level: Int,
        state: State,
        visitor: ElmLibraryVisitor<Value?, State?>,
        emit: () -> Unit,
    ) {
        val source = sources[level]
        val visit = { index: Int ->
            bind(level, index)
            if (
                singleSourceConjuncts[level].all { it.isTrue(visitor, state) } &&
                    plan.multiSourceConjuncts[level].all {
                        isTrue(visitor.visitExpression(it, state))
                    }
            ) {
                if (level == sources.lastIndex) emit() else descend(level + 1, state, visitor, emit)
            }
            // Visit every combination
            false
        }
        val equiJoin = plan.equiJoins[level]
        if (equiJoin == null) {
            source.indices.forEach { visit(it) }
        } else {
            anyCandidate(
                visitor.visitExpression(equiJoin.outer, state),
                source.size,
                {
                    joinIndexes[level]
                        ?: JoinIndex(source.size) {
                                bind(level, it)
                                joinKeyOf(visitor.visitExpression(equiJoin.inner, state))
                            }
                            .also { joinIndexes[level] = it }
                },
                visit,
            )
        }
    }

    private fun bind(level: Int, index: Int) {
        variables[level].value = sources[level][index]
        singleSourceConjuncts[level].forEach { it.index = index }
        subexpressions[level].forEach { it.index = index }
    }
}
