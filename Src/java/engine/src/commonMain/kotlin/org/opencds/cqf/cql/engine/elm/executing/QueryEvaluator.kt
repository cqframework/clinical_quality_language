package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.*
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable
import org.opencds.cqf.cql.engine.runtime.CqlList
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.iterators.QueryIterator

object QueryEvaluator {
    fun ensureIterable(source: Any?): Iterable<Any?> {
        if (source is Iterable<*>) {
            return source
        } else {
            val sourceList = mutableListOf<Any?>()
            if (source != null) sourceList.add(source)
            return sourceList
        }
    }

    private fun evaluateLets(
        elm: Query?,
        state: State?,
        letVariables: kotlin.collections.List<Variable?>,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ) {
        for (i in 0..<elm!!.let.size) {
            letVariables[i]!!.value = visitor.visitExpression(elm.let[i].expression!!, state)
        }
    }

    private fun evaluateRelationships(
        elm: Query,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Boolean {
        // TODO: This is the most naive possible implementation here, but it should
        // perform okay with 1) caching and 2) small data sets
        var shouldInclude = true
        for (relationship in elm.relationship) {
            var hasSatisfyingData = false
            val relatedSourceData =
                ensureIterable(visitor.visitExpression(relationship.expression!!, state))
            for (relatedElement in relatedSourceData) {
                state!!.push(Variable(relationship.alias!!).withValue(relatedElement))
                try {
                    val satisfiesRelatedCondition =
                        visitor.visitExpression(relationship.suchThat!!, state)
                    if (
                        (relationship is With || relationship is Without) &&
                            true == satisfiesRelatedCondition
                    ) {
                        hasSatisfyingData = true
                        break // Once we have detected satisfying data, no need to continue testing
                    }
                } finally {
                    state.pop()
                }
            }

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
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Boolean {
        if (elm.where != null) {
            val satisfiesCondition = visitor.visitExpression(elm.where!!, state)
            if (!(satisfiesCondition is Boolean && satisfiesCondition)) {
                return false
            }
        }

        return true
    }

    private fun evaluateAggregate(
        elm: AggregateClause,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
        elements: MutableList<Any?>,
    ): MutableList<Any?> {
        return mutableListOf(AggregateClauseEvaluator.aggregate(elm, state, visitor, elements))
    }

    private fun constructTuple(variables: MutableList<Variable>): Tuple {
        val elementMap = mutableMapOf<String, Any?>()
        for (v in variables) {
            elementMap[v.name!!] = v.value
        }

        return Tuple().withElements(elementMap)
    }

    fun sortResult(
        elm: Query,
        result: MutableList<Any?>,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ) {
        val sortClause = elm.sort

        if (sortClause != null) {
            for (byItem in sortClause.by) {
                when (byItem) {
                    is ByExpression ->
                        result.sortWith(
                            CqlList(state, visitor, "\$this", byItem.expression!!).expressionSort
                        )

                    is ByColumn -> result.sortWith(CqlList(state, byItem.path).columnSort)

                    else -> result.sortWith(CqlList(state).valueSort)
                }

                val direction = byItem.direction!!.value()
                if (direction == "desc" || direction == "descending") {
                    result.reverse()
                }
            }
        }
    }

    fun internalEvaluate(
        elm: Query,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        if (elm.aggregate != null && elm.`return` != null) {
            throw CqlException("aggregate and return are mutually exclusive")
        }

        val sources = ArrayList<Iterator<Any?>>()
        val variables = ArrayList<Variable>()
        val letVariables = ArrayList<Variable?>()
        var result = mutableListOf<Any?>()
        var sourceIsList = false
        var pushCount = 0
        try {
            for (source in elm.source) {
                val obj = visitor.visitExpression(source.expression!!, state)
                val querySource = QuerySource(source.alias, obj)
                sources.add(querySource.data!!.iterator())
                if (querySource.isList) {
                    sourceIsList = true
                }
                val variable = Variable(source.alias!!)
                variables.add(variable)
                state!!.push(variable)
                pushCount++
            }

            for (let in elm.let) {
                val letVariable = Variable(let.identifier!!)
                letVariables.add(letVariable)
                state!!.push(letVariable)
                pushCount++
            }

            val iterator = QueryIterator(state, sources)

            while (iterator.hasNext()) {
                // We need type metadata tracking in the CQL engine
                // to narrow this down better
                @Suppress("UNCHECKED_CAST") val elements = iterator.next() as MutableList<Any?>

                // Assign  variables
                assignVariables(variables, elements)

                evaluateLets(elm, state, letVariables, visitor)

                if (!evaluateRelationships(elm, state, visitor)) {
                    continue
                }

                if (!evaluateWhere(elm, state, visitor)) {
                    continue
                }

                // There's a "return" clause in the CQL
                if (elm.`return` != null) {
                    result.add(visitor.visitExpression(elm.`return`!!.expression!!, state))
                } else if (elm.aggregate != null || variables.size > 1) {
                    result.add(constructTuple(variables))
                } else {
                    result.add(elements[0])
                }
            }
        } finally {
            while (pushCount > 0) {
                state!!.pop()
                pushCount--
            }
        }

        if (
            elm.`return` != null &&
                elm.`return`!!.isDistinct() == true &&
                !state!!.engineOptions.contains(CqlEngine.Options.EnableHedisCompatibilityMode)
        ) {
            result = DistinctEvaluator.distinct(result, state)!!.toMutableList()
        }

        if (elm.aggregate != null) {
            result = evaluateAggregate(elm.aggregate!!, state, visitor, result)
        }

        sortResult(elm, result, state, visitor)

        if ((result.isEmpty()) && !sourceIsList) {
            return null
        }

        return if (elm.aggregate != null || !sourceIsList) result[0] else result
    }

    private fun assignVariables(variables: MutableList<Variable>, elements: MutableList<Any?>) {
        for (i in variables.indices) {
            variables[i].value = elements[i]
        }
    }

    internal class QuerySource(val alias: String?, data: Any?) {
        val isList: Boolean
        val data: Iterable<Any?>?

        init {
            this.isList = data is Iterable<*>
            this.data = ensureIterable(data)
        }
    }
}
