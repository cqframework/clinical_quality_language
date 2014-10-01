package org.cqframework.cql.poc.translator.model;

import org.hl7.elm.r1.Expression;

// Note: This class is only used as a place-holder during resolution in a translator (or compiler...)
public class LibraryRef extends Expression {
    private String libraryName;

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String value) {
        libraryName = value;
    }
}
