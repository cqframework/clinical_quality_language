package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.VersionedIdentifier

class ElmConjunctiveRequirement(libraryIdentifier: VersionedIdentifier, expression: Expression) :
    ElmExpressionRequirement(libraryIdentifier, expression) {
    @JvmField val arguments: MutableList<ElmExpressionRequirement?> = ArrayList()

    override fun combine(requirement: ElmRequirement?): ElmExpressionRequirement {
        when (requirement) {
            is ElmConjunctiveRequirement -> {
                for (argument in requirement.arguments) {
                    arguments.add(argument)
                }
            }

            is ElmDisjunctiveRequirement -> {
                // Conjunction of disjunctions, too complex for analysis (i.e. not in DNF)
                return ElmExpressionRequirement(this.libraryIdentifier, this.expression)
            }

            is ElmExpressionRequirement -> {
                arguments.add(requirement)
            }

            is ElmRequirements -> {
                for (r in requirement.getRequirements()) {
                    combine(r)
                }
            }
        }
        return this
    }
}
