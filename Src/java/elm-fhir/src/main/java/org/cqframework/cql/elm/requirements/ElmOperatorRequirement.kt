package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.VersionedIdentifier

class ElmOperatorRequirement(libraryIdentifier: VersionedIdentifier?, expression: Expression?) :
    ElmExpressionRequirement(libraryIdentifier, expression) {
    private val requirements: HashSet<ElmRequirement?> = LinkedHashSet()

    fun getRequirements(): Iterable<ElmRequirement?> {
        return requirements
    }

    override fun combine(requirement: ElmRequirement?): ElmExpressionRequirement {
        if (requirement is ElmExpressionRequirement) {
            requirements.add(requirement)
        } else if (requirement is ElmRequirements) {
            for (r in requirement.requirements) {
                requirements.add(r)
            }
        }
        return this
    }

    override val isLiteral: Boolean
        /**
         * An operator expression is literal if it is all of its operands are literal and it has no
         * parameter or external data access
         *
         * @return
         */
        get() {
            // TODO: Determine parameter or external data access within the operator or function
            // body
            var isLiteral = true
            for (r in requirements) {
                if (!(r is ElmExpressionRequirement && r.isLiteral)) {
                    isLiteral = false
                }
            }
            return isLiteral
        }
}
