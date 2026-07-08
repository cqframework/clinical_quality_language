package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.Exists
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Not
import org.hl7.elm.r1.VersionedIdentifier

class ElmOperatorRequirement(libraryIdentifier: VersionedIdentifier, expression: Expression) :
    ElmExpressionRequirement(libraryIdentifier, expression) {
    private val requirements: HashSet<ElmRequirement?> = LinkedHashSet()

    fun getRequirements(): Iterable<ElmRequirement?> {
        return requirements
    }

    override fun combine(requirement: ElmRequirement?): ElmExpressionRequirement {
        if (requirement is ElmExpressionRequirement) {
            requirement.determineSelectivity()
            if (requirement is ElmQueryRequirement) {
                selectivity = requirement.selectivity
                determineSelectivity()
            }
            if (requirement is ElmDataRequirement) {
                selectivity = requirement.selectivity
                determineSelectivity()
            }
            if (requirement is ElmOperatorRequirement) {
                if (requirement.element is Exists) {
                    selectivity = requirement.selectivity
                    determineSelectivity()
                }
            }
            requirements.add(requirement)
        } else if (requirement is ElmRequirements) {
            for (r in requirement.getRequirements()) {
                requirements.add(r)
            }
        }
        return this
    }

    override fun determineSelectivity(): ElmQuerySelectivity? {
        if (element is Exists && selectivity != null) {
            if (selectivity!!.inclusivity == null) {
                selectivity!!.inclusivity = ElmQuerySelectivity.Inclusivity.INCLUSION
            } else if (selectivity!!.inclusivity != ElmQuerySelectivity.Inclusivity.INCLUSION) {
                selectivity!!.inclusivity = ElmQuerySelectivity.Inclusivity.INDETERMINATE
            }
        } else if (element is Not && selectivity != null) {
            if (selectivity!!.inclusivity == ElmQuerySelectivity.Inclusivity.INCLUSION) {
                selectivity!!.inclusivity = ElmQuerySelectivity.Inclusivity.EXCLUSION
            } else {
                selectivity!!.inclusivity = ElmQuerySelectivity.Inclusivity.INDETERMINATE
            }
        } else if (selectivity != null && selectivity!!.inclusivity != null) {
            selectivity!!.inclusivity = ElmQuerySelectivity.Inclusivity.INDETERMINATE
        }
        return selectivity
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
