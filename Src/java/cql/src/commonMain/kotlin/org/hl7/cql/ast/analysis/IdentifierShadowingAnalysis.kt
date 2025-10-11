package org.hl7.cql.ast.analysis

import org.hl7.cql.ast.Identifier
import org.hl7.cql.ast.Locator
import org.hl7.cql.ast.Problem
import org.hl7.cql.ast.ProblemSeverity

class IdentifierShadowingAnalysis : AstAnalysis {

    private val problems = mutableListOf<Problem>()

    override fun reset() {
        problems.clear()
    }

    override fun onDeclaration(identifier: Identifier, locator: Locator, previous: Locator?) {
        if (previous != null) {
            problems +=
                Problem(
                    message =
                        "Identifier '${identifier.value}' shadows a previous declaration (${describeLocator(previous)}).",
                    locator = locator,
                    severity = ProblemSeverity.WARNING,
                )
        }
    }

    override fun collectProblems(): List<Problem> = problems.toList()

    private fun describeLocator(locator: Locator): String =
        when {
            locator.line != null && locator.column != null ->
                "line ${locator.line}, column ${locator.column}"
            locator.line != null -> "line ${locator.line}"
            locator.startIndex >= 0 -> "index ${locator.startIndex}"
            else -> "unknown location"
        }
}
