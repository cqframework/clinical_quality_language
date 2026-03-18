package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.CaseChildren
import org.hl7.cql.ast.CaseExpression
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CaseItem
import org.hl7.elm.r1.Expression as ElmExpression

/**
 * Emit a CaseExpression as an ELM Case node. Purely mechanical — choice type wrapping, null-As
 * wrapping, and implicit type promotion are all inserted by ConversionInserter before emission.
 *
 * Children (comparand, case conditions/results, elseResult) are pre-folded by the catamorphism.
 */
internal fun EmissionContext.emitCaseExpression(
    expression: CaseExpression,
    comparandElm: ElmExpression?,
    casesElm: List<CaseChildren<ElmExpression>>,
    elseElm: ElmExpression,
): ElmExpression {
    return Case().apply {
        comparandElm?.let { comparand = it }
        caseItem =
            casesElm
                .map { c ->
                    CaseItem().apply {
                        `when` = c.condition
                        then = c.result
                    }
                }
                .toMutableList()
        `else` = elseElm
    }
}
