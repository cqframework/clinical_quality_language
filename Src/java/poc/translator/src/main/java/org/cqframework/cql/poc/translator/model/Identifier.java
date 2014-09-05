package org.cqframework.cql.poc.translator.model;

import org.hl7.elm.r1.Expression;

public class Identifier extends Expression {
    private String identifier;
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String value) {
        identifier = value;
    }

    private String libraryName;
    public String getLibraryName() { return libraryName; }
    public void setLibraryName(String value) { libraryName = value; }
}
