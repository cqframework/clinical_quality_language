package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.AggregateClause
import org.hl7.elm.r1.ByColumn
import org.hl7.elm.r1.ByExpression
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.RelationshipClause
import org.hl7.elm.r1.SortDirection
import org.hl7.elm.r1.With
import org.hl7.elm.r1.Without
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.SortHelper
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.iterators.QueryIterator
import org.opencds.cqf.cql.engine.runtime.toCqlList

object QueryEvaluator {
    fun ensureIterable(source: Value?): Iterable<Value?> {
        if (source is List) {
            return source
        } else {
            val sourceList = mutableListOf<Value?>()
            if (source != null) sourceList.add(source)
            return sourceList
        }
    }

    private fun evaluateLets(
        elm: Query?,
        state: State?,
        letVariables: kotlin.collections.List<Variable?>,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ) {
        for (i in 0..<elm!!.let.size) {
            letVariables[i]!!.value = visitor.visitExpression(elm.let[i].expression!!, state)
        }
    }

    private fun evaluateRelationships(
        elm: Query,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
        runtimes: kotlin.collections.List<RelationshipRuntime?>?,
    ): kotlin.Boolean {
        var shouldInclude = true
        for ((i, relationship) in elm.relationship.withIndex()) {
            val hasSatisfyingData =
                runtimes?.get(i)?.hasSatisfyingData(state!!, visitor)
                    ?: hasSatisfyingData(relationship, state, visitor)

            if (
                (relationship is With && !hasSatisfyingData) ||
                    (relationship is Without && hasSatisfyingData)
            ) {
                shouldInclude = false
                break // Once we have determined the row should not be included, no need to continue
                // testing other related information
            }
        }

        return shouldInclude
    }

    private fun evaluateWhere(
        elm: Query,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): kotlin.Boolean {
        if (elm.where != null) {
            val satisfiesCondition = visitor.visitExpression(elm.where!!, state)
            if (!(satisfiesCondition is Boolean && satisfiesCondition.value)) {
                return false
            }
        }

        return true
    }

    private fun evaluateAggregate(
        elm: AggregateClause,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
        elements: MutableList<Value?>,
    ): MutableList<Value?> {
        return mutableListOf(
            AggregateClauseEvaluator.aggregate(elm, state, visitor, elements.toCqlList())
        )
    }

    private fun constructTuple(variables: kotlin.collections.List<Variable>): Tuple {
        val elementMap = mutableMapOf<kotlin.String, Value?>()
        for (v in variables) {
            elementMap[v.name!!] = v.value
        }

        return Tuple().withElements(elementMap)
    }

    fun sortResult(
        elm: Query,
        result: MutableList<Value?>,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ) {
        val sortClause = elm.sort

        if (sortClause != null) {
            val comparator =
                sortClause.by
                    .map { byItem ->
                        val baseComparator =
                            when (byItem) {
                                is ByExpression ->
                                    Comparator<Value?> { left, right ->
                                        val alias = "\$this"

                                        var leftResult: Value? = null
                                        try {
                                            state!!.push(Variable(alias).withValue(left))
                                            leftResult =
                                                visitor.visitExpression(byItem.expression!!, state)
                                        } finally {
                                            state!!.pop()
                                        }

                                        var rightResult: Value? = null
                                        try {
                                            state.push(Variable(alias).withValue(right))
                                            rightResult =
                                                visitor.visitExpression(byItem.expression!!, state)
                                        } finally {
                                            state.pop()
                                        }

                                        SortHelper.compare(leftResult, rightResult, state)
                                    }
                                is ByColumn ->
                                    Comparator<Value?> { left, right ->
                                        val leftCol =
                                            PropertyEvaluator.resolvePath(left, byItem.path!!)
                                        val rightCol =
                                            PropertyEvaluator.resolvePath(right, byItem.path!!)

                                        SortHelper.compare(leftCol, rightCol, state)
                                    }
                                else ->
                                    Comparator<Value?> { left, right ->
                                        SortHelper.compare(left, right, state)
                                    }
                            }

                        val direction = byItem.direction
                        val isDesc =
                            direction == SortDirection.DESC || direction == SortDirection.DESCENDING

                        if (isDesc) baseComparator.reversed() else baseComparator
                    }
                    .reduceOrNull { acc, comp -> acc.then(comp) }

            comparator?.let { result.sortWith(it) }
        }
    }

    fun internalEvaluate(
        elm: Query,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): Value? {
        if (elm.aggregate != null && elm.`return` != null) {
            throw CqlException("aggregate and return are mutually exclusive")
        }

        val plan =
            if (state!!.isQueryOptimizationEnabled) {
                state.cache.queryPlans.getOrPut(elm) { QueryPlan(elm, state) }
            } else null
        var relationshipRuntimes: kotlin.collections.List<RelationshipRuntime?>? = null

        val querySources = mutableListOf<QuerySource>()
        val sources = mutableListOf<Iterator<Value?>>()
        val variables = mutableListOf<Variable>()
        val letVariables = mutableListOf<Variable?>()
        var result = mutableListOf<Value?>()
        var sourceIsList = false
        var pushCount = 0
        try {
            for (source in elm.source) {
                val obj = visitor.visitExpression(source.expression!!, state)
                val querySource = QuerySource(source.alias, obj)
                querySources.add(querySource)
                sources.add(querySource.data.iterator())
                if (querySource.isList) {
                    sourceIsList = true
                }
                val variable = Variable(source.alias!!)
                variables.add(variable)
                state.push(variable)
                pushCount++
            }

            for (let in elm.let) {
                val letVariable = Variable(let.identifier!!)
                letVariables.add(letVariable)
                state.push(letVariable)
                pushCount++
            }

            relationshipRuntimes =
                plan?.relationships?.map { it?.let { RelationshipRuntime(it, state) } }

            val crossProduct = plan?.crossProduct
            if (crossProduct != null) {
                CrossProductRuntime(crossProduct, querySources.map { it.data.toList() }, variables)
                    .evaluate(state, visitor) { addRow(elm, state, visitor, variables, result) }
            }

            // The cross product plan has already produced the rows
            val iterator =
                if (crossProduct == null) QueryIterator(state, sources)
                else emptyList<MutableList<Value?>>().iterator()

            while (iterator.hasNext()) {
                val elements = iterator.next() // as MutableList<CqlType?>

                // Assign  variables
                assignVariables(variables, elements)

                evaluateLets(elm, state, letVariables, visitor)

                if (!evaluateRelationships(elm, state, visitor, relationshipRuntimes)) {
                    continue
                }

                if (!evaluateWhere(elm, state, visitor)) {
                    continue
                }

                addRow(elm, state, visitor, variables, result)
            }
        } finally {
            relationshipRuntimes?.forEach { it?.close(state) }
            while (pushCount > 0) {
                state.pop()
                pushCount--
            }
        }

        if (
            elm.`return` != null &&
                elm.`return`!!.isDistinct() == true &&
                !state.engineOptions.contains(CqlEngine.Options.EnableHedisCompatibilityMode)
        ) {
            result = DistinctEvaluator.distinct(result.toCqlList(), state)!!.toMutableList()
        }

        if (elm.aggregate != null) {
            result = evaluateAggregate(elm.aggregate!!, state, visitor, result)
        }

        sortResult(elm, result, state, visitor)

        if ((result.isEmpty()) && !sourceIsList) {
            return null
        }

        return if (elm.aggregate != null || !sourceIsList) result[0] else result.toCqlList()
    }

    private fun addRow(
        elm: Query,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
        variables: kotlin.collections.List<Variable>,
        result: MutableList<Value?>,
    ) {
        // There's a "return" clause in the CQL
        if (elm.`return` != null) {
            result.add(visitor.visitExpression(elm.`return`!!.expression!!, state))
        } else if (elm.aggregate != null || variables.size > 1) {
            result.add(constructTuple(variables))
        } else {
            result.add(variables[0].value)
        }
    }

    private fun assignVariables(variables: MutableList<Variable>, elements: MutableList<Value?>) {
        for (i in variables.indices) {
            variables[i].value = elements[i]
        }
    }

    internal class QuerySource(val alias: kotlin.String?, data: Value?) {
        val isList = data is List
        val data = ensureIterable(data)
    }
}

/** The naive nested loop, re-evaluating the related source and condition for every row. */
private fun hasSatisfyingData(
    relationship: RelationshipClause,
    state: State?,
    visitor: ElmLibraryVisitor<Value?, State?>,
): kotlin.Boolean {
    val relatedSourceData =
        QueryEvaluator.ensureIterable(visitor.visitExpression(relationship.expression!!, state))
    // Once we have detected satisfying data, no need to continue testing
    return relatedSourceData.any { relatedElement ->
        state!!.push(Variable(relationship.alias!!).withValue(relatedElement))
        try {
            true == (visitor.visitExpression(relationship.suchThat!!, state) as? Boolean)?.value
        } finally {
            state.pop()
        }
    }
}
