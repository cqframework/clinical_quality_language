package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.VersionedIdentifier;

public class ElmJoinRequirement extends ElmExpressionRequirement {
    public ElmJoinRequirement(VersionedIdentifier libraryIdentifier, Expression expression, ElmPropertyRequirement leftProperty, ElmPropertyRequirement rightProperty) {
        super(libraryIdentifier, expression);

        if (leftProperty == null) {
            throw new IllegalArgumentException("leftProperty is required");
        }
        this.leftProperty = leftProperty;

        if (rightProperty == null) {
            throw new IllegalArgumentException("rightProperty is required");
        }
        this.rightProperty = rightProperty;
    }

    protected ElmPropertyRequirement leftProperty;
    public ElmPropertyRequirement getLeftProperty() {
        return this.leftProperty;
    }

    protected ElmPropertyRequirement rightProperty;
    public ElmPropertyRequirement getRightProperty() {
        return this.rightProperty;
    }
}
