package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.VersionedIdentifier

class ElmPropertyRequirement(
    libraryIdentifier: VersionedIdentifier,
    property: Property,
    source: Element,
    inCurrentScope: Boolean,
) : ElmExpressionRequirement(libraryIdentifier, property) {
    val property: Property?
        get() = this.element as Property?

    var source: Element?
        private set

    var inCurrentScope: Boolean
        private set

    init {
        this.source = source
        this.inCurrentScope = inCurrentScope
    }
}
