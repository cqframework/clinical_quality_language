package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.cql.ast.ExpressionQuerySource
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.ast.SortDirection as AstSortDirection
import org.hl7.cql.ast.UnsupportedExpression
import org.hl7.cql.ast.WithClause as AstWithClause
import org.hl7.cql.ast.WithoutClause as AstWithoutClause
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.AggregateClause as ElmAggregateClause
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.AliasedQuerySource as ElmAliasedQuerySource
import org.hl7.elm.r1.ByColumn
import org.hl7.elm.r1.ByDirection
import org.hl7.elm.r1.ByExpression
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.LetClause as ElmLetClause
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.RelationshipClause
import org.hl7.elm.r1.ReturnClause as ElmReturnClause
import org.hl7.elm.r1.SortByItem as ElmSortByItem
import org.hl7.elm.r1.SortClause as ElmSortClause
import org.hl7.elm.r1.SortDirection as ElmSortDirection
import org.hl7.elm.r1.Tuple as ElmTuple
import org.hl7.elm.r1.TupleElement as ElmTupleElement
import org.hl7.elm.r1.With
import org.hl7.elm.r1.Without

/** Emit a [QueryExpression] as an ELM [Query] node. */
internal fun EmissionContext.emitQuery(expression: QueryExpression): ElmExpression {
    val query = Query()

    // Sources
    for (source in expression.sources) {
        query.source.add(emitAliasedQuerySource(source))
    }

    // Let clauses
    for (letItem in expression.lets) {
        query.let.add(emitLetClause(letItem))
    }

    // Relationship clauses (with/without)
    for (inclusion in expression.inclusions) {
        query.relationship.add(emitRelationshipClause(inclusion))
    }

    // Where
    expression.where?.let { query.`where` = emitExpression(it) }

    // Aggregate
    expression.aggregate?.let { query.aggregate = emitAggregateClause(it) }

    // Return — synthesize a Tuple return for multi-source queries without explicit return
    if (expression.result != null) {
        query.`return` = emitReturnClause(expression.result!!)
    } else if (expression.sources.size > 1 && expression.aggregate == null) {
        query.`return` = synthesizeMultiSourceReturn(expression)
    }

    // Sort
    expression.sort?.let { query.sort = emitSortClause(it) }

    return query
}

/**
 * Synthesize a return clause with a Tuple of AliasRefs for multi-source queries that have no
 * explicit return clause. This matches legacy translator behavior which creates a Tuple with one
 * element per source alias.
 */
private fun EmissionContext.synthesizeMultiSourceReturn(
    expression: QueryExpression
): ElmReturnClause {
    val tuple = ElmTuple()
    for (source in expression.sources) {
        val alias = source.alias.value
        val element = ElmTupleElement().withName(alias)
        val aliasRef = AliasRef().withName(alias)
        // Set resultType on aliasRef from the source expression type
        val sourceExpr =
            when (val qs = source.source) {
                is ExpressionQuerySource -> qs.expression
                is RetrieveExpression -> qs
            }
        val sourceExprType = semanticModel[sourceExpr]
        if (sourceExprType != null) {
            val elementType =
                if (sourceExprType is ListType) sourceExprType.elementType else sourceExprType
            decorate(aliasRef, elementType)
        }
        element.value = aliasRef
        tuple.element.add(element)
    }
    val ret = ElmReturnClause()
    ret.distinct = true
    ret.expression = tuple
    return ret
}

private fun EmissionContext.emitAliasedQuerySource(
    source: org.hl7.cql.ast.AliasedQuerySource
): ElmAliasedQuerySource {
    val elmSource = ElmAliasedQuerySource()
    elmSource.alias = source.alias.value
    val querySource = source.source
    when (querySource) {
        is ExpressionQuerySource -> {
            elmSource.expression = emitExpression(querySource.expression)
        }
        is RetrieveExpression -> {
            elmSource.expression = emitExpression(querySource)
        }
    }
    // Set resultType on the source — the legacy sets it to the source expression type
    val exprType = elmSource.expression?.resultType
    if (exprType != null) {
        decorate(elmSource, exprType)
    }
    return elmSource
}

private fun EmissionContext.emitLetClause(letItem: org.hl7.cql.ast.LetClauseItem): ElmLetClause {
    val elmLet = ElmLetClause()
    elmLet.identifier = letItem.identifier.value
    elmLet.expression = emitExpression(letItem.expression)
    val letType = semanticModel[letItem.expression]
    if (letType != null) {
        decorate(elmLet, letType)
    }
    return elmLet
}

private fun EmissionContext.emitRelationshipClause(
    inclusion: org.hl7.cql.ast.QueryInclusionClause
): RelationshipClause {
    return when (inclusion) {
        is AstWithClause -> {
            val withElm = With()
            withElm.alias = inclusion.source.alias.value
            withElm.expression = emitQuerySourceExpression(inclusion.source)
            withElm.suchThat = emitExpression(inclusion.condition)
            val sourceType = withElm.expression?.resultType
            if (sourceType != null) decorate(withElm, sourceType)
            withElm
        }
        is AstWithoutClause -> {
            val withoutElm = Without()
            withoutElm.alias = inclusion.source.alias.value
            withoutElm.expression = emitQuerySourceExpression(inclusion.source)
            withoutElm.suchThat = emitExpression(inclusion.condition)
            val sourceType = withoutElm.expression?.resultType
            if (sourceType != null) decorate(withoutElm, sourceType)
            withoutElm
        }
    }
}

private fun EmissionContext.emitQuerySourceExpression(
    source: org.hl7.cql.ast.AliasedQuerySource
): ElmExpression {
    val querySource = source.source
    return when (querySource) {
        is ExpressionQuerySource -> emitExpression(querySource.expression)
        is RetrieveExpression -> emitExpression(querySource)
    }
}

private fun EmissionContext.emitReturnClause(ret: org.hl7.cql.ast.ReturnClause): ElmReturnClause {
    val elmReturn = ElmReturnClause()
    elmReturn.expression = emitExpression(ret.expression)
    // Only set distinct when explicitly specified (all→false, distinct→true).
    // When neither keyword is present, both all and distinct are false in the AST,
    // and we leave distinct unset so it defaults to true in the ELM class.
    if (ret.all) {
        elmReturn.distinct = false
    } else if (ret.distinct) {
        elmReturn.distinct = true
    }
    // Set resultType on return clause
    val retExprType = semanticModel[ret.expression]
    if (retExprType != null) {
        decorate(elmReturn, ListType(retExprType))
    }
    return elmReturn
}

private fun EmissionContext.emitSortClause(sort: org.hl7.cql.ast.SortClause): ElmSortClause {
    val elmSort = ElmSortClause()
    for (item in sort.items) {
        elmSort.`by`.add(emitSortByItem(item))
    }
    return elmSort
}

private fun EmissionContext.emitSortByItem(item: org.hl7.cql.ast.SortByItem): ElmSortByItem {
    val direction = mapSortDirection(item.direction)
    // The Builder uses an UnsupportedExpression sentinel for bare `sort asc/desc`.
    return when {
        item.expression is UnsupportedExpression ->
            ByDirection().withDirection(direction) as ElmSortByItem
        // Simple identifier sort (e.g., `sort by id`) → ByColumn with path
        item.expression is IdentifierExpression -> {
            val name = (item.expression as IdentifierExpression).name.simpleName
            ByColumn().withPath(name).withDirection(direction) as ElmSortByItem
        }
        else -> {
            val byExpr = ByExpression().withDirection(direction)
            byExpr.expression = emitExpression(item.expression)
            byExpr as ElmSortByItem
        }
    }
}

private fun mapSortDirection(direction: AstSortDirection): ElmSortDirection {
    return when (direction) {
        AstSortDirection.ASC -> ElmSortDirection.ASC
        AstSortDirection.ASCENDING -> ElmSortDirection.ASCENDING
        AstSortDirection.DESC -> ElmSortDirection.DESC
        AstSortDirection.DESCENDING -> ElmSortDirection.DESCENDING
    }
}

private fun EmissionContext.emitAggregateClause(
    agg: org.hl7.cql.ast.AggregateClause
): ElmAggregateClause {
    val elmAgg = ElmAggregateClause()
    elmAgg.identifier = agg.identifier.value
    if (agg.distinct) {
        elmAgg.distinct = true
    }
    elmAgg.expression = emitExpression(agg.expression)
    if (agg.starting != null) {
        elmAgg.starting = emitExpression(agg.starting!!)
    } else {
        // Legacy always emits a Null starting value when none is specified
        elmAgg.starting = org.hl7.elm.r1.Null()
    }
    val aggType = semanticModel[agg.expression]
    if (aggType != null) {
        decorate(elmAgg, aggType)
    }
    return elmAgg
}
