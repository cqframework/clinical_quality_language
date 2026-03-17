package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.CaseExpression
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CaseItem
import org.hl7.elm.r1.Expression as ElmExpression

/**
 * Emit a CaseExpression as an ELM Case node. Supports both condition-style (no comparand) and
 * comparand-style case expressions. When branches have incompatible types, wraps each branch in an
 * As(ChoiceTypeSpecifier).
 */
internal fun EmissionContext.emitCaseExpression(expression: CaseExpression): ElmExpression {
    val choiceType = computeCaseChoiceType(expression)
    return Case().apply {
        expression.comparand?.let { comparand = emitExpression(it) }

        caseItem =
            expression.cases
                .map { item ->
                    CaseItem().apply {
                        `when` = emitExpression(item.condition)
                        val thenElm = emitExpression(item.result)
                        then = choiceType?.let { wrapCaseAs(thenElm, it) } ?: thenElm
                    }
                }
                .toMutableList()

        val elseElm = emitExpression(expression.elseResult)
        `else` = choiceType?.let { wrapCaseAs(elseElm, it) } ?: elseElm
    }
}

private fun EmissionContext.computeCaseChoiceType(expression: CaseExpression): ChoiceType? {
    val anyType = operatorRegistry.type("Any")
    val branchExprs = expression.cases.map { it.result } + expression.elseResult
    val types = branchExprs.mapNotNull { semanticModel[it] }.filter { it != anyType }
    if (types.size < 2) return null
    val distinct = types.distinct()
    if (distinct.size < 2) return null
    if (distinct.all { t -> distinct.any { other -> other != t && other.isSuperTypeOf(t) } }) {
        return null
    }
    val common = distinct.reduce { acc, type -> acc.getCommonSuperTypeOf(type) }
    if (common != anyType && common != DataType.ANY) return null
    return ChoiceType(distinct)
}

private fun EmissionContext.wrapCaseAs(
    expression: ElmExpression,
    choiceType: ChoiceType,
): ElmExpression {
    return As().apply {
        operand = expression
        asTypeSpecifier = operatorRegistry.typeBuilder.dataTypeToTypeSpecifier(choiceType)
    }
}
