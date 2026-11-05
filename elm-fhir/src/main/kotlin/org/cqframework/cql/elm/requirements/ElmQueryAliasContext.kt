package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.VersionedIdentifier

class ElmQueryAliasContext(
    private val libraryIdentifier: VersionedIdentifier,
    @JvmField val querySource: AliasedQuerySource,
) {

    val alias: String?
        get() = querySource.alias

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
        this.requirements!!.querySource = this.querySource
    }

    fun reportProperty(propertyRequirement: ElmPropertyRequirement) {
        requirements!!.reportProperty(propertyRequirement)
    }
}
