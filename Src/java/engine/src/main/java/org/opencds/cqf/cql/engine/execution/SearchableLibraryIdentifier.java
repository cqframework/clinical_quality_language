package org.opencds.cqf.cql.engine.execution;

import java.util.Objects;
import java.util.StringJoiner;
import org.hl7.elm.r1.VersionedIdentifier;

// LUKETODO: javadoc
public class SearchableLibraryIdentifier {
    private final String identifierId;

    public static SearchableLibraryIdentifier fromIdentifier(VersionedIdentifier identifier) {
        return new SearchableLibraryIdentifier(identifier.getId());
    }

    // Visible for testing
    public static SearchableLibraryIdentifier fromId(String id) {
        return new SearchableLibraryIdentifier(id);
    }

    private SearchableLibraryIdentifier(String identifierId) {
        this.identifierId = identifierId;
    }

    public boolean matches(VersionedIdentifier identifier) {
        return identifierId.equals(identifier.getId());
    }

    public boolean matches(String id) {
        return identifierId.equals(id);
    }

    public VersionedIdentifier toIdentifier() {
        return new VersionedIdentifier().withId(identifierId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchableLibraryIdentifier that = (SearchableLibraryIdentifier) o;
        return Objects.equals(identifierId, that.identifierId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifierId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SearchableLibraryIdentifier.class.getSimpleName() + "[", "]")
                .add("identifierId='" + identifierId + "'")
                .toString();
    }
}
