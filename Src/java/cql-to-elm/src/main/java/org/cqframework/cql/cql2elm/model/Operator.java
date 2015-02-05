package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;

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
