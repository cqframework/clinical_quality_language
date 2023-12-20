package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.VersionedIdentifier;

public class ElmRequirement {

    protected VersionedIdentifier libraryIdentifier;

    public VersionedIdentifier getLibraryIdentifier() {
        return this.libraryIdentifier;
    }

    protected Element element;

    public Element getElement() {
        return this.element;
    }

    public ElmRequirement(VersionedIdentifier libraryIdentifier, Element element) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is required");
        }

        if (element == null) {
            throw new IllegalArgumentException("element is required");
        }

        this.libraryIdentifier = libraryIdentifier;
        this.element = element;
    }

    public boolean hasRequirement(ElmRequirement requirement) {
        return requirement != null && requirement.getElement() == element;
    }

    @Override
    public int hashCode() {
        return 47 + (39 * libraryIdentifier.hashCode()) + (53 * element.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ElmRequirement) {
            ElmRequirement that = (ElmRequirement) obj;
            return this.libraryIdentifier.equals(that.libraryIdentifier) && this.element == that.element;
        }

        return false;
    }
}
