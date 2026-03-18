package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.CaseChildren
import org.hl7.cql.ast.CaseExpression
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CaseItem
import org.hl7.elm.r1.Expression as ElmExpression

/**
 * Emit a CaseExpression as an ELM Case node. Purely mechanical — all conversions (choice type
 * wrapping, null-As wrapping, implicit type promotion) are handled by ConversionInserter in the
 * analysis phase via the INFER→CONVERT→CHECK loop.
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
