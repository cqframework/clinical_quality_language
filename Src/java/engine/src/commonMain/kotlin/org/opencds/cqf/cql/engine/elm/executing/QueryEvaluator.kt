package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.AggregateClause
import org.hl7.elm.r1.ByColumn
import org.hl7.elm.r1.ByExpression
import org.hl7.elm.r1.Query
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
    ): kotlin.Boolean {
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
                            true == (satisfiesRelatedCondition as? Boolean)?.value
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

    private fun constructTuple(variables: MutableList<Variable>): Tuple {
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
            for (byItem in sortClause.by) {
                when (byItem) {
                    is ByExpression ->
                        result.sortWith { left, right ->
                            val alias = "\$this"

                            var leftResult: Value? = null
                            try {
                                state!!.push(Variable(alias).withValue(left))
                                leftResult = visitor.visitExpression(byItem.expression!!, state)
                            } finally {
                                state!!.pop()
                            }

                            var rightResult: Value? = null
                            try {
                                state.push(Variable(alias).withValue(right))
                                rightResult = visitor.visitExpression(byItem.expression!!, state)
                            } finally {
                                state.pop()
                            }

                            SortHelper.compare(leftResult, rightResult, state)
                        }

                    is ByColumn ->
                        result.sortWith { left, right ->
                            val leftCol = PropertyEvaluator.resolvePath(left, byItem.path!!)
                            val rightCol = PropertyEvaluator.resolvePath(right, byItem.path!!)

                            SortHelper.compare(leftCol, rightCol, state)
                        }

                    else ->
                        result.sortWith { left, right -> SortHelper.compare(left, right, state) }
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
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): Value? {
        if (elm.aggregate != null && elm.`return` != null) {
            throw CqlException("aggregate and return are mutually exclusive")
        }

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
                sources.add(querySource.data.iterator())
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
                val elements = iterator.next() // as MutableList<CqlType?>

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
