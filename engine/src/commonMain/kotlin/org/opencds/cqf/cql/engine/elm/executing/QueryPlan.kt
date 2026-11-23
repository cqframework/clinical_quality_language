package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.BaseElmLibraryVisitor
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor
import org.hl7.elm.r1.Aggregate
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.And
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Current
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.Filter
import org.hl7.elm.r1.ForEach
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.IdentifierRef
import org.hl7.elm.r1.If
import org.hl7.elm.r1.Iteration
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Message
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.QueryLetRef
import org.hl7.elm.r1.RelationshipClause
import org.hl7.elm.r1.Repeat
import org.hl7.elm.r1.Total
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State

/**
 * How a query can avoid re-evaluating work that does not change between the rows of a join.
 *
 * Every evaluation the plan performs is one the naive nested loop would also perform: conjuncts and
 * subexpressions are computed lazily, the first time the loop needs them, and subexpressions are
 * only taken from positions that are evaluated whenever their conjunct is (never from under an
 * `if`, `case`, `coalesce` or a nested query or iteration). The plan can therefore skip
 * evaluations, but cannot introduce errors. Clauses that could log a message, which the naive loop
 * would repeat, are left to the naive loop.
 */
internal class QueryPlan(query: Query, state: State) {
    /** One per relationship clause, in order; null where the clause is evaluated naively. */
    val relationships: List<RelationshipPlan?>

    /** Present for a multi-source query with a where clause and no let or relationship clauses. */
    val crossProduct: CrossProductPlan?

    init {
        val effects = EffectAnalysis(state)
        val outerNames =
            (query.source.map { it.alias!! } + query.let.map { it.identifier!! }).toSet()
        relationships =
            query.relationship.map { relationship ->
                if (
                    effects.mayHaveEffect(relationship.expression!!) ||
                        effects.mayHaveEffect(relationship.suchThat!!)
                ) {
                    null
                } else {
                    RelationshipPlan(relationship, outerNames)
                }
            }
        crossProduct =
            if (isCrossProduct(query) && !effects.mayHaveEffect(query.where!!)) {
                CrossProductPlan(query)
            } else null
    }

    private fun isCrossProduct(query: Query): kotlin.Boolean =
        query.source.size > 1 &&
            query.where != null &&
            query.let.isEmpty() &&
            query.relationship.isEmpty()
}

/** A `with` or `without` clause, split by which side of the join each part depends on. */
internal class RelationshipPlan(val relationship: RelationshipClause, outerNames: Set<String>) {
    /** True if the related source does not depend on the row, so it is evaluated only once. */
    val isSourceInvariant = dependenciesOf(relationship.expression!!, outerNames).isEmpty()

    /** Conjuncts that depend only on the row: evaluated once per row. */
    val rowConjuncts = mutableListOf<Expression>()

    /** Conjuncts that depend only on the related element: evaluated once per element. */
    val relatedConjuncts = mutableListOf<Expression>()

    /** Conjuncts that depend on both, evaluated per pair. */
    val pairConjuncts = mutableListOf<Expression>()

    /** Subexpressions of [pairConjuncts] that depend only on the row. */
    val rowSubexpressions = mutableListOf<Expression>()

    /** Subexpressions of [pairConjuncts] that depend only on the related element. */
    val relatedSubexpressions = mutableListOf<Expression>()

    init {
        val alias = relationship.alias!!
        val names = outerNames + alias
        for (conjunct in conjunctsOf(relationship.suchThat!!)) {
            val dependencies = dependenciesOf(conjunct, names)
            when {
                alias !in dependencies -> rowConjuncts.add(conjunct)
                dependencies == setOf(alias) -> relatedConjuncts.add(conjunct)
                else -> pairConjuncts.add(conjunct)
            }
        }
        for (conjunct in pairConjuncts) {
            collectSubexpressions(conjunct, names) { subexpression, dependencies ->
                when {
                    alias !in dependencies -> rowSubexpressions.add(subexpression)
                    dependencies == setOf(alias) -> relatedSubexpressions.add(subexpression)
                    else -> return@collectSubexpressions false
                }
                true
            }
        }
    }
}

/**
 * The where clause of `from A, B, ... where ...`, split by the deepest source each conjunct depends
 * on, so a combination is abandoned as soon as a conjunct fails.
 */
internal class CrossProductPlan(query: Query) {
    private val aliases = query.source.map { it.alias!! }

    /** Conjuncts that depend on no source. */
    val invariantConjuncts = mutableListOf<Expression>()

    /** Per source, conjuncts that depend on that source alone. */
    val singleSourceConjuncts = aliases.map { mutableListOf<Expression>() }

    /** Per source, conjuncts that depend on it and on earlier sources. */
    val multiSourceConjuncts = aliases.map { mutableListOf<Expression>() }

    /** Per source, subexpressions of multi-source conjuncts that depend on that source alone. */
    val singleSourceSubexpressions = aliases.map { mutableListOf<Expression>() }

    init {
        val names = aliases.toSet()
        for (conjunct in conjunctsOf(query.where!!)) {
            val dependencies = dependenciesOf(conjunct, names)
            when (dependencies.size) {
                0 -> invariantConjuncts.add(conjunct)
                1 -> singleSourceConjuncts[aliases.indexOf(dependencies.single())].add(conjunct)
                else -> {
                    multiSourceConjuncts[dependencies.maxOf { aliases.indexOf(it) }].add(conjunct)
                    collectSubexpressions(conjunct, names) { subexpression, dependencies ->
                        if (dependencies.size != 1) return@collectSubexpressions false
                        singleSourceSubexpressions[aliases.indexOf(dependencies.single())].add(
                            subexpression
                        )
                        true
                    }
                }
            }
        }
    }
}

/**
 * Determines whether evaluating an expression could log a message or call an external function,
 * following function and expression definitions across libraries.
 */
private class EffectAnalysis(private val state: State) {
    private val definitionEffects = state.cache.definitionEffects

    private val visitor =
        FunctionalElmVisitor.from<Unit, kotlin.Boolean>(
            { elm, _ ->
                when (elm) {
                    is Message -> true
                    is FunctionRef -> mayHaveEffect(elm)
                    is ExpressionRef -> mayHaveEffect(elm)
                    else -> false
                }
            },
            { a, b -> a || b },
        )

    fun mayHaveEffect(expression: Expression): kotlin.Boolean =
        visitor.visitExpression(expression, Unit)

    private fun mayHaveEffect(functionRef: FunctionRef): kotlin.Boolean =
        inLibrary(functionRef.libraryName) {
            val library = state.getCurrentLibrary()!!
            Libraries.getFunctionDefs(functionRef.name, library)
                .filter {
                    if (functionRef.signature.isEmpty()) {
                        it.operand.size == functionRef.operand.size
                    } else {
                        FunctionRefEvaluator.functionDefOperandsSignatureEqual(
                            it,
                            functionRef.signature,
                        )
                    }
                }
                .any { true == it.isExternal() || mayHaveEffect(it) }
        }

    private fun mayHaveEffect(expressionRef: ExpressionRef): kotlin.Boolean =
        inLibrary(expressionRef.libraryName) {
            mayHaveEffect(
                Libraries.resolveExpressionRef(expressionRef.name, state.getCurrentLibrary()!!)
            )
        }

    private fun mayHaveEffect(definition: ExpressionDef): kotlin.Boolean =
        definitionEffects[definition]
            ?: run {
                // Definitions cannot be recursive; this only guards against looping on bad ELM
                definitionEffects[definition] = true
                val body = definition.expression
                (body != null && mayHaveEffect(body)).also { definitionEffects[definition] = it }
            }

    /** Runs [block] with the referenced library current, assuming an effect if it fails. */
    private fun inLibrary(libraryName: String?, block: () -> kotlin.Boolean): kotlin.Boolean {
        @Suppress("TooGenericExceptionCaught")
        return try {
            val enteredLibrary = state.enterLibrary(libraryName)
            try {
                block()
            } finally {
                state.exitLibrary(enteredLibrary)
            }
        } catch (_: RuntimeException) {
            true
        }
    }
}

/** The operands of nested `and`s; the expression is true exactly when all of them are. */
internal fun conjunctsOf(expression: Expression): List<Expression> =
    if (expression is And) expression.operand.flatMap(::conjunctsOf) else listOf(expression)

private val referencedNamesCollector =
    FunctionalElmVisitor.from<MutableSet<String>, Unit>({ elm, names ->
        when (elm) {
            is AliasRef -> elm.name?.let(names::add)
            is QueryLetRef -> elm.name?.let(names::add)
            is IdentifierRef -> elm.name?.let(names::add)
            is OperandRef -> elm.name?.let(names::add)
            is Property -> elm.scope?.let(names::add)
            is Current -> elm.scope?.let(names::add)
            is Iteration -> elm.scope?.let(names::add)
            is Total -> elm.scope?.let(names::add)
        }
    })

/**
 * The names among [names] that [element] may refer to. This over-approximates (a nested query that
 * reuses an alias name counts as a reference), which only ever prevents an optimization.
 */
internal fun dependenciesOf(element: Element, names: Set<String>): Set<String> {
    val referenced = mutableSetOf<String>()
    referencedNamesCollector.visitElement(element, referenced)
    return referenced.intersect(names)
}

/** Expressions whose children are evaluated conditionally or once per element. */
private fun isLazy(expression: Expression): kotlin.Boolean =
    expression is If ||
        expression is Case ||
        expression is Coalesce ||
        expression is Query ||
        expression is Filter ||
        expression is ForEach ||
        expression is Repeat ||
        expression is Aggregate

/** Expressions that are as cheap to evaluate as to look up. */
private fun isTrivial(expression: Expression): kotlin.Boolean =
    expression is AliasRef ||
        expression is QueryLetRef ||
        expression is OperandRef ||
        expression is IdentifierRef ||
        expression is Literal ||
        expression is Null ||
        (expression is Property && expression.source == null)

/**
 * Offers [accept] the largest non-trivial subexpressions of [expression] that depend on some of
 * [names], descending only through children that are evaluated whenever their parent is. [accept]
 * returns false to have the subexpression's children considered instead.
 */
private fun collectSubexpressions(
    expression: Expression,
    names: Set<String>,
    accept: (Expression, Set<String>) -> kotlin.Boolean,
) {
    object : BaseElmLibraryVisitor<Unit, Unit>() {
            override fun defaultResult(elm: Element, context: Unit) = Unit

            override fun visitExpression(elm: Expression, context: Unit) {
                // The conjunct itself is evaluated as a whole
                if (elm !== expression && !isTrivial(elm)) {
                    val dependencies = dependenciesOf(elm, names)
                    if (dependencies.isNotEmpty() && accept(elm, dependencies)) {
                        return
                    }
                }
                if (!isLazy(elm)) {
                    super.visitExpression(elm, context)
                }
            }
        }
        .visitExpression(expression, Unit)
}
