package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.VersionedIdentifier;

public class ElmConditionRequirement extends ElmExpressionRequirement {
    public ElmConditionRequirement(VersionedIdentifier libraryIdentifier, Expression expression, ElmPropertyRequirement property, ElmExpressionRequirement comparand) {
        super(libraryIdentifier, expression);

        if (property == null) {
            throw new IllegalArgumentException("property is required");
        }
        this.property = property;

        if (comparand == null) {
            throw new IllegalArgumentException("comparand is required");
        }
        this.comparand = comparand;
    }

    protected ElmPropertyRequirement property;
    public ElmPropertyRequirement getProperty() {
        return this.property;
    }

    protected ElmExpressionRequirement comparand;
    public ElmExpressionRequirement getComparand() {
        return this.comparand;
    }
}
