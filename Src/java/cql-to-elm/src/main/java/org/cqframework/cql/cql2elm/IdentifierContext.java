package org.cqframework.cql.cql2elm;

import java.util.Objects;
import java.util.StringJoiner;
import org.cqframework.cql.elm.tracking.Trackable;

/**
 * Simple POJO using for identifier hider that maintains the identifier and Trackable type of the construct being evaluated.
 */
public class IdentifierContext {
    private final String identifier;
    private final Class<? extends Trackable> elementSubclass;

    public IdentifierContext(String identifier, Class<? extends Trackable> elementSubclass) {
        this.identifier = identifier;
        this.elementSubclass = elementSubclass;
    }

    public String getIdentifier() {
        return identifier;
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
        return Objects.equals(identifier, that.identifier) && Objects.equals(elementSubclass, that.elementSubclass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, elementSubclass);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IdentifierContext.class.getSimpleName() + "[", "]")
                .add("identifier='" + identifier + "'")
                .add("elementSubclass=" + elementSubclass)
                .toString();
    }
}
