package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.Trackable;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Simple POJO using for identifier hider that maintains the identifier and Trackable type of the construct being evaluated.
 */
public class HidingIdentifierContext {
    private final String identifier;
    private final Class<? extends Trackable> elementSubclass;

    public HidingIdentifierContext(String theIdentifier, Class<? extends Trackable> theElementSubclass) {
        identifier = theIdentifier;
        elementSubclass = theElementSubclass;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Class<? extends Trackable> getTrackableSubclass() {
        return elementSubclass;
    }

    @Override
    public boolean equals(Object theO) {
        if (this == theO) {
            return true;
        }
        if (theO == null || getClass() != theO.getClass()) {
            return false;
        }
        HidingIdentifierContext that = (HidingIdentifierContext) theO;
        return Objects.equals(identifier, that.identifier) && Objects.equals(elementSubclass, that.elementSubclass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, elementSubclass);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HidingIdentifierContext.class.getSimpleName() + "[", "]")
                .add("identifier='" + identifier + "'")
                .add("elementSubclass=" + elementSubclass)
                .toString();
    }
}
