package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.AliasedQuerySource
import org.hl7.cql.ast.ExpressionQuerySource
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.TupleType
import org.hl7.cql.model.TupleTypeElement

@Suppress("ReturnCount")
internal fun TypeResolver.inferQueryType(expression: QueryExpression): DataType? {
    // Build scope from sources
    val scope = mutableMapOf<String, Resolution>()
    for (source in expression.sources) {
        val elementType = inferSourceElementType(source)
        // Always add alias to scope even without a resolved type, so inner expressions
        // can resolve identifiers (e.g., for property access and sort-by)
        val aliasType = elementType ?: (type("Any") ?: continue)
        scope[source.alias.value] = Resolution.AliasRef(source.alias.value, aliasType)
    }

    // Push scope for lets, where, inclusions, return, aggregate
    pushQueryScope(scope)
    try {
        // Resolve let clause types and add to scope
        for (letItem in expression.lets) {
            val letType = inferType(letItem.expression)
            if (letType != null) {
                scope[letItem.identifier.value] =
                    Resolution.QueryLetRef(letItem.identifier.value, letType)
            }
        }

        // Resolve inclusion clauses
        for (inclusion in expression.inclusions) {
            inferInclusionType(inclusion)
        }

        // Resolve where
        expression.where?.let { inferType(it) }

        // Resolve aggregate
        expression.aggregate?.let { agg ->
            val aggType = inferAggregateType(agg, scope)
            resolveSortItems(expression)
            return aggType
        }

        // Resolve return
        val resultType =
            expression.result?.let { ret ->
                val retType = inferType(ret.expression) ?: return null
                ListType(retType)
            }
                ?: run {
                    if (expression.sources.size > 1) {
                        // Multi-source query without return: synthesize Tuple type from aliases
                        val elements =
                            expression.sources.mapNotNull { src ->
                                val elemType = inferSourceElementType(src) ?: return@mapNotNull null
                                TupleTypeElement(src.alias.value, elemType)
                            }
                        if (elements.size != expression.sources.size) return null
                        ListType(TupleType(elements))
                    } else {
                        // Single-source: result is List<sourceType>
                        val sourceType =
                            expression.sources.firstOrNull()?.let { inferSourceElementType(it) }
                                ?: return null
                        ListType(sourceType)
                    }
                }

        // Resolve sort (runs regardless of whether return clause is present)
        resolveSortItems(expression)

        return resultType
    } finally {
        popQueryScope()
    }
}

internal fun TypeResolver.inferSourceElementType(source: AliasedQuerySource): DataType? {
    val querySource = source.source
    val sourceType =
        when (querySource) {
            is ExpressionQuerySource -> inferType(querySource.expression)
            is RetrieveExpression -> inferType(querySource)
        } ?: return null
    // Unwrap ListType to get element type
    return if (sourceType is ListType) sourceType.elementType else sourceType
}

private fun TypeResolver.inferInclusionType(inclusion: org.hl7.cql.ast.QueryInclusionClause) {
    val (source, condition) =
        when (inclusion) {
            is org.hl7.cql.ast.WithClause -> inclusion.source to inclusion.condition
            is org.hl7.cql.ast.WithoutClause -> inclusion.source to inclusion.condition
        }
    val elementType = inferSourceElementType(source) ?: return
    val innerScope =
        mapOf(source.alias.value to Resolution.AliasRef(source.alias.value, elementType))
    pushQueryScope(innerScope)
    try {
        inferType(condition)
    } finally {
        popQueryScope()
    }
}

private fun TypeResolver.resolveSortItems(expression: QueryExpression) {
    expression.sort?.let { sort ->
        for (item in sort.items) {
            inferType(item.expression)
        }
    }
}

@Suppress("ReturnCount")
private fun TypeResolver.inferAggregateType(
    agg: org.hl7.cql.ast.AggregateClause,
    scope: MutableMap<String, Resolution>,
): DataType? {
    // Resolve starting expression type
    val startingType = agg.starting?.let { inferType(it) } ?: type("Any")

    // Add accumulator to scope — legacy uses QueryLetRef for the accumulator identifier
    if (startingType != null) {
        scope[agg.identifier.value] = Resolution.QueryLetRef(agg.identifier.value, startingType)
    }

    return inferType(agg.expression)
}
