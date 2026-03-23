package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.StringEscapeUtils.unescapeCql
import org.cqframework.cql.cql2elm.analysis.Resolution
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.OperandRef
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.QueryLetRef
import org.hl7.elm.r1.ValueSetRef

/**
 * Emit an [IdentifierExpression] by looking up its resolution in the TypeTable and producing the
 * appropriate ELM reference node: [ExpressionRef], [ParameterRef], [OperandRef], [AliasRef],
 * [QueryLetRef], or a terminology reference ([CodeSystemRef], [ValueSetRef], [CodeRef],
 * [ConceptRef]).
 */
internal fun EmissionContext.emitIdentifierExpression(
    expression: IdentifierExpression
): ElmExpression {
    val resolution = semanticModel.getIdentifierResolution(expression)
    return when (resolution) {
        is Resolution.ExpressionRef ->
            ExpressionRef().withName(unescapeCql(resolution.definition.name.value))
        is Resolution.ParameterRef ->
            ParameterRef().withName(unescapeCql(resolution.definition.name.value))
        is Resolution.OperandRef -> OperandRef().withName(resolution.name)
        is Resolution.AliasRef -> AliasRef().withName(resolution.name)
        is Resolution.QueryLetRef -> QueryLetRef().withName(resolution.name)
        is Resolution.CodeSystemRef ->
            CodeSystemRef().withName(unescapeCql(resolution.definition.name.value))
        is Resolution.ValueSetRef ->
            ValueSetRef().withName(unescapeCql(resolution.definition.name.value)).apply {
                preserve = true
            }
        is Resolution.CodeRef -> CodeRef().withName(unescapeCql(resolution.definition.name.value))
        is Resolution.ConceptRef ->
            ConceptRef().withName(unescapeCql(resolution.definition.name.value))
        is Resolution.ContextRef -> ExpressionRef().withName(resolution.definition.context.value)
        is Resolution.IncludeRef ->
            // Library alias identifiers are never emitted standalone — they appear as targets of
            // property access or function calls. The parent emission handles libraryName. If we
            // get here, the fold is processing the target before the parent sees it; return a
            // placeholder that will be discarded.
            org.hl7.elm.r1.IdentifierRef().withName(expression.name.simpleName)
        null ->
            // Unresolved identifiers become IdentifierRef — the legacy translator emits these
            // for sort-by property paths and other contexts where the identifier refers to a
            // property of the implicit result element.
            org.hl7.elm.r1.IdentifierRef().withName(expression.name.simpleName)
    }
}
