package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.AliasedQuerySource
import org.hl7.cql.ast.ExpressionQuerySource
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType

@Suppress("ReturnCount")
internal fun TypeResolver.inferQueryType(
    expression: QueryExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    // Build scope from sources
    val scope = mutableMapOf<String, Resolution>()
    for (source in expression.sources) {
        val elementType = inferSourceElementType(source, typeTable, symbolTable) ?: continue
        scope[source.alias.value] = Resolution.AliasRef(source.alias.value, elementType)
    }

    // Push scope for lets, where, inclusions, return, aggregate
    pushQueryScope(scope)
    try {
        // Resolve let clause types and add to scope
        for (letItem in expression.lets) {
            val letType = inferType(letItem.expression, typeTable, symbolTable)
            if (letType != null) {
                scope[letItem.identifier.value] =
                    Resolution.QueryLetRef(letItem.identifier.value, letType)
            }
        }

        // Resolve inclusion clauses
        for (inclusion in expression.inclusions) {
            inferInclusionType(inclusion, typeTable, symbolTable)
        }

        // Resolve where
        expression.where?.let { inferType(it, typeTable, symbolTable) }

        // Resolve aggregate
        expression.aggregate?.let { agg ->
            val aggType = inferAggregateType(agg, scope, typeTable, symbolTable)
            resolveSortItems(expression, typeTable, symbolTable)
            return aggType
        }

        // Resolve return
        val resultType =
            expression.result?.let { ret ->
                val retType = inferType(ret.expression, typeTable, symbolTable) ?: return null
                ListType(retType)
            }
                ?: run {
                    // No return clause: result is List<sourceType>
                    if (expression.sources.size > 1) {
                        throw UnsupportedOperationException(
                            "Multi-source queries without return clause are not yet supported."
                        )
                    }
                    val sourceType =
                        expression.sources.firstOrNull()?.let {
                            inferSourceElementType(it, typeTable, symbolTable)
                        } ?: return null
                    ListType(sourceType)
                }

        // Resolve sort (runs regardless of whether return clause is present)
        resolveSortItems(expression, typeTable, symbolTable)

        return resultType
    } finally {
        popQueryScope()
    }
}

internal fun TypeResolver.inferSourceElementType(
    source: AliasedQuerySource,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    val querySource = source.source
    val sourceType =
        when (querySource) {
            is ExpressionQuerySource -> inferType(querySource.expression, typeTable, symbolTable)
            else -> null
        } ?: return null
    // Unwrap ListType to get element type
    return if (sourceType is ListType) sourceType.elementType else sourceType
}

private fun TypeResolver.inferInclusionType(
    inclusion: org.hl7.cql.ast.QueryInclusionClause,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
) {
    val (source, condition) =
        when (inclusion) {
            is org.hl7.cql.ast.WithClause -> inclusion.source to inclusion.condition
            is org.hl7.cql.ast.WithoutClause -> inclusion.source to inclusion.condition
        }
    val elementType = inferSourceElementType(source, typeTable, symbolTable) ?: return
    val innerScope =
        mapOf(source.alias.value to Resolution.AliasRef(source.alias.value, elementType))
    pushQueryScope(innerScope)
    try {
        inferType(condition, typeTable, symbolTable)
    } finally {
        popQueryScope()
    }
}

private fun TypeResolver.resolveSortItems(
    expression: QueryExpression,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
) {
    expression.sort?.let { sort ->
        for (item in sort.items) {
            inferType(item.expression, typeTable, symbolTable)
        }
    }
}

@Suppress("ReturnCount")
private fun TypeResolver.inferAggregateType(
    agg: org.hl7.cql.ast.AggregateClause,
    scope: MutableMap<String, Resolution>,
    typeTable: TypeTable,
    symbolTable: SymbolTable,
): DataType? {
    // Resolve starting expression type
    val startingType = agg.starting?.let { inferType(it, typeTable, symbolTable) } ?: type("Any")

    // Add accumulator to scope — legacy uses AliasRef for the accumulator identifier
    if (startingType != null) {
        scope[agg.identifier.value] = Resolution.AliasRef(agg.identifier.value, startingType)
    }

    return inferType(agg.expression, typeTable, symbolTable)
}
