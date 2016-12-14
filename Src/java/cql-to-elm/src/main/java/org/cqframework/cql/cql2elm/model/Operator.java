package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;
import org.hl7.elm.r1.AccessModifier;

public class Operator {
    public Operator(String name, Signature signature, DataType resultType) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is null or empty");
        }

        if (signature == null) {
            throw new IllegalArgumentException("signature is null");
        }

        if (resultType == null) {
            throw new IllegalArgumentException("resultType is null");
        }

        this.name = name;
        this.signature = signature;
        this.resultType = resultType;
    }

    private String libraryName;
    public String getLibraryName() {
        return this.libraryName;
    }
    public void setLibraryName(String libraryName) {
        if (libraryName == null || libraryName.equals("")) {
            throw new IllegalArgumentException("libraryName is null.");
        }

        this.libraryName = libraryName;
    }

    private AccessModifier accessLevel = AccessModifier.PUBLIC;
    public AccessModifier getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessModifier accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Operator withAccessLevel(AccessModifier accessLevel) {
        setAccessLevel(accessLevel);
        return this;
    }

    private String name;
    public String getName() {
        return this.name;
    }

    private Signature signature;
    public Signature getSignature() {
        return this.signature;
    }

    private DataType resultType;
    public DataType getResultType() {
        return this.resultType;
    }
}
