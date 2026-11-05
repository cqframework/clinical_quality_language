package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.Element
import org.hl7.elm.r1.VersionedIdentifier

open class ElmRequirement(val libraryIdentifier: VersionedIdentifier, val element: Element) {
    open fun hasRequirement(requirement: ElmRequirement?): Boolean {
        return requirement != null && requirement.element === element
    }

    override fun hashCode(): Int {
        return 47 + (39 * libraryIdentifier.hashCode()) + (53 * element.hashCode())
    }

    override fun equals(other: Any?): Boolean {
        if (other is ElmRequirement) {
            return this.libraryIdentifier == other.libraryIdentifier &&
                this.element === other.element
        }

        return false
    }
}
