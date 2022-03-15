package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.*;

public class ElmExpressionRequirement extends ElmRequirement {
    public ElmExpressionRequirement(VersionedIdentifier libraryIdentifier, Expression expression) {
        super(libraryIdentifier, expression);
    }

    public Expression getExpression() {
        return (Expression)this.element;
    }

    public Expression getElement() {
        return getExpression();
    }

    public ElmExpressionRequirement combine(ElmExpressionRequirement requirement) {
        return this;
    }

    public boolean isLiteral() {
        return this.element instanceof Literal;
    }

    public boolean isTerminologyReference() {
        return this.element instanceof ValueSetRef
                || this.element instanceof CodeSystemRef
                || this.element instanceof ConceptRef
                || this.element instanceof CodeRef;
    }

    public boolean isParameterReference() {
        return this.element instanceof ParameterRef;
    }
}
