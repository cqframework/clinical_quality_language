package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.VersionedIdentifier

class ElmDisjunctiveRequirement(libraryIdentifier: VersionedIdentifier, expression: Expression) :
    ElmExpressionRequirement(libraryIdentifier, expression) {
    val arguments: MutableList<ElmExpressionRequirement?> = ArrayList()

    override fun combine(requirement: ElmRequirement?): ElmExpressionRequirement {
        when (requirement) {
            is ElmDisjunctiveRequirement -> {
                for (argument in requirement.arguments) {
                    arguments.add(argument)
                }
            }

            is ElmConjunctiveRequirement -> {
                arguments.add(requirement as ElmExpressionRequirement)
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
