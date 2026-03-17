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
 * - Implicit type promotion (Integer→Decimal) across branches
 * - Null-As wrapping for null branches
 * - Choice type wrapping when branches have incompatible types
 * - Comparand-style when-clause conversions
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
    val anyType = operatorRegistry.type("Any")

    // Compute the result type from non-null branches for null-As wrapping and type promotion
    // Use the overall expression type (which considers implicit conversions) rather than
    // just the first branch type
    val resultType = semanticModel[expression]?.let { if (it == anyType) null else it }

    // For comparand-style cases, determine comparand type for when-clause conversions
    val comparandType = expression.comparand?.let { semanticModel[it] }

    return Case().apply {
        comparandElm?.let { comparand = it }

        caseItem =
            expression.cases
                .mapIndexed { index, item ->
                    CaseItem().apply {
                        var whenElm = casesElm[index].condition
                        val thenElm = casesElm[index].result

                        // Comparand when-clause: convert when value to comparand type
                        if (comparandType != null) {
                            val whenType = semanticModel[item.condition]
                            if (
                                whenType != null && whenType != comparandType && whenType != anyType
                            ) {
                                whenElm = applyImplicitConversion(whenElm, whenType, comparandType)
                            } else if (whenType == anyType) {
                                // Null when in comparand case: wrap in As(comparandType)
                                whenElm = wrapNullAs(whenElm, comparandType)
                            }
                        }

                        `when` = whenElm
                        then =
                            if (choiceType != null) {
                                wrapCaseAs(thenElm, choiceType)
                            } else {
                                wrapCaseBranch(
                                    thenElm,
                                    semanticModel[item.result],
                                    resultType,
                                    anyType,
                                )
                            }
                    }
                }
                .toMutableList()

        `else` =
            if (choiceType != null) {
                wrapCaseAs(elseElm, choiceType)
            } else {
                wrapCaseBranch(elseElm, semanticModel[expression.elseResult], resultType, anyType)
            }
    }
}

/** Wrap a case branch expression: apply null-As wrapping or implicit type promotion as needed. */
private fun EmissionContext.wrapCaseBranch(
    expression: ElmExpression,
    branchType: DataType?,
    resultType: DataType?,
    anyType: DataType,
): ElmExpression {
    if (resultType == null) return expression
    // Null branch: wrap in As(resultType)
    if (branchType == anyType) {
        return wrapNullAs(expression, resultType)
    }
    // Type promotion: apply implicit conversion (e.g., Integer→Decimal)
    if (branchType != null && branchType != resultType) {
        return applyImplicitConversion(expression, branchType, resultType)
    }
    return expression
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
