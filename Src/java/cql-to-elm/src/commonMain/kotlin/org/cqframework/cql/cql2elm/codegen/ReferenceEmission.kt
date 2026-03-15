package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.analysis.Resolution
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.QueryLetRef

/**
 * Emit an [IdentifierExpression] by looking up its resolution in the TypeTable and producing the
 * appropriate ELM reference node: [ExpressionRef], [ParameterRef], [OperandRef], [AliasRef], or
 * [QueryLetRef].
 */
internal fun EmissionContext.emitIdentifierExpression(
    expression: IdentifierExpression
): ElmExpression {
    val resolution = typeTable.getIdentifierResolution(expression)
    return when (resolution) {
        is Resolution.ExpressionRef -> ExpressionRef().withName(resolution.definition.name.value)
        is Resolution.ParameterRef -> ParameterRef().withName(resolution.definition.name.value)
        is Resolution.OperandRef -> OperandRef().withName(resolution.name)
        is Resolution.AliasRef -> AliasRef().withName(resolution.name)
        is Resolution.QueryLetRef -> QueryLetRef().withName(resolution.name)
        is Resolution.ContextRef ->
            throw ElmEmitter.UnsupportedNodeException("Context references are not yet supported.")
        null ->
            throw ElmEmitter.UnsupportedNodeException(
                "Unresolved identifier: '${expression.name.simpleName}'"
            )
    }
}
