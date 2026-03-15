package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.CaseExpression
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CaseItem
import org.hl7.elm.r1.Expression as ElmExpression

/**
 * Emit a CaseExpression as an ELM Case node. Supports both condition-style (no comparand) and
 * comparand-style case expressions.
 */
internal fun EmissionContext.emitCaseExpression(expression: CaseExpression): ElmExpression {
    return Case().apply {
        expression.comparand?.let { comparand = emitExpression(it) }

        caseItem =
            expression.cases
                .map { item ->
                    CaseItem().apply {
                        `when` = emitExpression(item.condition)
                        then = emitExpression(item.result)
                    }
                }
                .toMutableList()

        `else` = emitExpression(expression.elseResult)
    }
}
