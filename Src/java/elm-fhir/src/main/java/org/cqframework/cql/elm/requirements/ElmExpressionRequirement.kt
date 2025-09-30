package org.cqframework.cql.elm.requirements

import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.VersionedIdentifier

open class ElmExpressionRequirement(
    libraryIdentifier: VersionedIdentifier,
    expression: Expression,
) : ElmRequirement(libraryIdentifier, expression) {
    open val expression: Expression
        get() = this.element as Expression

    fun getElement(): Expression {
        return this.expression
    }

    open fun combine(requirement: ElmRequirement?): ElmExpressionRequirement? {
        return this
    }

    open val isLiteral: Boolean
        get() = this.element is Literal

    val isTerminologyReference: Boolean
        get() =
            this.element is ValueSetRef ||
                this.element is CodeSystemRef ||
                this.element is ConceptRef ||
                this.element is CodeRef

    val isParameterReference: Boolean
        get() = this.element is ParameterRef
}
