package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Property;
import org.hl7.elm.r1.VersionedIdentifier;

public class ElmPropertyRequirement extends ElmExpressionRequirement {
    public ElmPropertyRequirement(VersionedIdentifier libraryIdentifier, Property property, Element source, boolean inCurrentScope) {
        super(libraryIdentifier, property);

        if (source == null) {
            throw new IllegalArgumentException("source is required");
        }

        this.source = source;
        this.inCurrentScope = inCurrentScope;
    }

    public Property getProperty() {
        return (Property)this.element;
    }

    public Property getElement() {
        return getProperty();
    }

    protected Element source;
    public Element getSource() {
        return source;
    }

    protected boolean inCurrentScope;
    public boolean getInCurrentScope() {
        return inCurrentScope;
    }
}
