package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.LetClause
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.VersionedIdentifier

class ElmQueryLetContext(
    private val libraryIdentifier: VersionedIdentifier,
    @JvmField val letClause: LetClause,
) {

    val identifier: String?
        get() = letClause.identifier

    private var requirements: ElmDataRequirement? = null

    fun getRequirements(): ElmDataRequirement {
        return requirements!!
    }

    fun setRequirements(requirements: ElmRequirement?) {
        when (requirements) {
            is ElmDataRequirement -> {
                this.requirements = requirements
            }

            is ElmExpressionRequirement -> {
                this.requirements = ElmDataRequirement.inferFrom(requirements)
            }

            else -> {
                // Should never land here, but defensively...
                this.requirements = ElmDataRequirement(this.libraryIdentifier, Retrieve())
            }
        }
        this.requirements!!.querySource = this.letClause
    }

    fun reportProperty(propertyRequirement: ElmPropertyRequirement) {
        requirements!!.reportProperty(propertyRequirement)
    }
}
