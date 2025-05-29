package org.cqframework.cql.cql2elm;

import java.util.Objects;
import java.util.StringJoiner;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.elm.r1.IdentifierRef;

/**
 * Simple POJO using for identifier hider that maintains the identifier and Trackable type of the construct being evaluated.
 */
public class IdentifierContext {
    private final IdentifierRef identifierRef;
    private final Class<? extends Trackable> elementSubclass;

    public IdentifierContext(IdentifierRef identifierRef, Class<? extends Trackable> elementSubclass) {
        this.identifierRef = identifierRef;
        this.elementSubclass = elementSubclass;
    }

    public IdentifierRef getIdentifierRef() {
        return identifierRef;
    }

    public String getIdentifier() {
        return identifierRef.getName();
    }

    public Class<? extends Trackable> getTrackableSubclass() {
        return elementSubclass;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        IdentifierContext that = (IdentifierContext) other;
        return Objects.equals(getIdentifier(), that.getIdentifier())
                && Objects.equals(elementSubclass, that.elementSubclass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier(), elementSubclass);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IdentifierContext.class.getSimpleName() + "[", "]")
                .add("identifier='" + getIdentifier() + "'")
                .add("elementSubclass=" + elementSubclass)
                .toString();
    }
}
