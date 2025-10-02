package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.VersionedIdentifier

open class ElmConstraintRequirement(
    libraryIdentifier: VersionedIdentifier,
    expression: Expression,
    leftProperty: ElmPropertyRequirement,
    rightProperty: ElmPropertyRequirement,
) : ElmExpressionRequirement(libraryIdentifier, expression) {
    var leftProperty: ElmPropertyRequirement?
        protected set

    var rightProperty: ElmPropertyRequirement?
        protected set

    init {
        this.leftProperty = leftProperty

        this.rightProperty = rightProperty
    }
}
