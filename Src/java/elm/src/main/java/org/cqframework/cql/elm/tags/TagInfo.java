package org.cqframework.cql.elm.tags;

import org.hl7.cql_annotations.r1.Locator;
import org.hl7.elm.r1.VersionedIdentifier;

public class TagInfo {

    private VersionedIdentifier library;
    private ElementType elementType;
    private String name;
    private String expressionName;
    private String value;
    private Locator locator;

    public TagInfo(VersionedIdentifier library, ElementType elementType, String name, String expressionName,  String value, Locator locator) {
        this.library = library;
        this.elementType = elementType;
        this.name = name;
        if (expressionName != null) { this.expressionName = expressionName; }
        if (value != null) { this.value = value; }
        if (locator != null) { this.locator = locator; }
    }

    public VersionedIdentifier library() {
        return this.library;
    }

    public ElementType elementType() {
        return this.elementType;
    }

    public String name() {
        return this.name;
    }

    public String expressionName() {
        return this.expressionName;
    }

    public String value() {
        return this.value;
    }

    public Locator locator() {
        return this.locator;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
        result = prime * result + ((library == null) ? 0 : library.hashCode());
        result = prime * result + ((locator == null) ? 0 : locator.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((expressionName == null) ? 0 : expressionName.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("java:S3776")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TagInfo other = (TagInfo) obj;
        if (elementType != other.elementType)
            return false;
        if (library == null) {
            if (other.library != null)
                return false;
        } else if (!library.equals(other.library))
            return false;
        if (locator == null) {
            if (other.locator != null)
                return false;
        } else if (!locator.equals(other.locator))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (expressionName == null) {
            if (other.expressionName != null)
                return false;
        } else if (!expressionName.equals(other.expressionName))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
