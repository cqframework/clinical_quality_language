package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.VersionedIdentifier

open class ElmConditionRequirement(
    libraryIdentifier: VersionedIdentifier,
    expression: Expression,
    property: ElmPropertyRequirement,
    comparand: ElmExpressionRequirement,
) : ElmExpressionRequirement(libraryIdentifier, expression) {
    var property: ElmPropertyRequirement?
        protected set

    var comparand: ElmExpressionRequirement?
        protected set

    init {
        this.property = property

        this.comparand = comparand
    }

    val isTargetable: Boolean
        get() =
            comparand != null &&
                (comparand!!.isLiteral ||
                    comparand!!.isTerminologyReference ||
                    comparand!!.isParameterReference)
}
