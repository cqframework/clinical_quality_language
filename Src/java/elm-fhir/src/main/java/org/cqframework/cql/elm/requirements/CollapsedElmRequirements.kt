package org.cqframework.cql.elm.requirements

class CollapsedElmRequirements {
    private val uniqueRequirements: MutableList<ElmRequirement> = ArrayList()

    @JvmField val requirementIdMap: MutableMap<String?, String?> = HashMap()

    fun getUniqueRequirements(): Iterable<ElmRequirement> {
        return uniqueRequirements
    }

    fun add(requirement: ElmRequirement) {
        val existing = getEquivalent(requirement)
        if (existing == null) {
            uniqueRequirements.add(requirement)
        } else {
            uniqueRequirements.remove(existing)
            val newRequirement = ComparableElmRequirement.mergeRequirements(existing, requirement)
            mapRequirementId(requirement, newRequirement)
            uniqueRequirements.add(
                ComparableElmRequirement.mergeRequirements(existing, requirement)
            )
        }
    }

    private fun mapRequirementId(oldRequirement: ElmRequirement, newRequirement: ElmRequirement) {
        if (oldRequirement.getElement().localId != null) {
            requirementIdMap[oldRequirement.getElement().localId] =
                newRequirement.getElement().localId
        }
    }

    fun getEquivalent(requirement: ElmRequirement): ElmRequirement? {
        for (existing in uniqueRequirements) {
            if (ComparableElmRequirement.requirementsEquivalent(existing, requirement)) {
                return existing
            }
        }

        return null
    }
}
