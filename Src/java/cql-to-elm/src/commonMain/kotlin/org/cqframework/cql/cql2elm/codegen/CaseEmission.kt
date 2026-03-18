package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.CaseChildren
import org.hl7.cql.ast.CaseExpression
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CaseItem
import org.hl7.elm.r1.Expression as ElmExpression

/**
 * Emit a CaseExpression as an ELM Case node. Supports both condition-style (no comparand) and
 * comparand-style case expressions. Handles:
 * - Choice type wrapping when branches have incompatible types (emission-level)
 *
 * Null-As wrapping and implicit type promotion are inserted by ConversionInserter before emission.
 *
 * Children (comparand, case conditions/results, elseResult) are pre-folded by the catamorphism.
 */
internal fun EmissionContext.emitCaseExpression(
    expression: CaseExpression,
    comparandElm: ElmExpression?,
    casesElm: List<CaseChildren<ElmExpression>>,
    elseElm: ElmExpression,
): ElmExpression {
    val choiceType = computeCaseChoiceType(expression)

    return Case().apply {
        comparandElm?.let { comparand = it }

        caseItem =
            expression.cases
                .mapIndexed { index, _ ->
                    CaseItem().apply {
                        `when` = casesElm[index].condition
                        then =
                            if (choiceType != null) {
                                wrapCaseAs(casesElm[index].result, choiceType)
                            } else {
                                casesElm[index].result
                            }
                    }
                }
                .toMutableList()

        `else` =
            if (choiceType != null) {
                wrapCaseAs(elseElm, choiceType)
            } else {
                elseElm
            }
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
    // Check if there exists a numeric promotion between types (e.g., Integer→Decimal)
    if (
        distinct.any { candidate ->
            distinct.all { t -> t == candidate || isNumericPromotion(t, candidate) }
        }
    ) {
        return null
    }
    val common = distinct.reduce { acc, type -> acc.getCommonSuperTypeOf(type) }
    if (common != anyType && common != DataType.ANY) return null
    return ChoiceType(distinct)
}

private fun isNumericPromotion(from: DataType, to: DataType): Boolean {
    val fromName = from.toString()
    val toName = to.toString()
    return (fromName == "System.Integer" && toName == "System.Long") ||
        (fromName == "System.Integer" && toName == "System.Decimal") ||
        (fromName == "System.Long" && toName == "System.Decimal")
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
