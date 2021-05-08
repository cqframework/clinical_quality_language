package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.VersionedIdentifier;

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
}
