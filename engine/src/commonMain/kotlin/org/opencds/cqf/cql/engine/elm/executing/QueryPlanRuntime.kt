package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Expression
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable
import org.opencds.cqf.cql.engine.runtime.Boolean
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

/** Evaluates a relationship clause following a [RelationshipPlan], for the rows of a query. */
internal class RelationshipRuntime(private val plan: RelationshipPlan, state: State) {
    private var invariantSource: List<Value?>? = null
    private val rowConjuncts = plan.rowConjuncts.map { HoistedExpression(it, 1) }
    private val rowSubexpressions =
        plan.rowSubexpressions.map { HoistedExpression(it, 1).also(state::hoist) }
    private var relatedConjuncts = emptyList<HoistedExpression>()
    private var relatedSubexpressions = emptyList<HoistedExpression>()

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
            related.indices.any { satisfies(it, related[it], state, visitor) }
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
        for (index in source.indices) {
            variables[level].value = source[index]
            singleSourceConjuncts[level].forEach { it.index = index }
            subexpressions[level].forEach { it.index = index }
            if (
                singleSourceConjuncts[level].all { it.isTrue(visitor, state) } &&
                    plan.multiSourceConjuncts[level].all {
                        isTrue(visitor.visitExpression(it, state))
                    }
            ) {
                if (level == sources.lastIndex) emit() else descend(level + 1, state, visitor, emit)
            }
        }
    }
}
