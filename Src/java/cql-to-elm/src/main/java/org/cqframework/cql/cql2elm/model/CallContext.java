package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;

public class CallContext {
    public CallContext(String libraryName, String operatorName, DataType... signature) {
        this.libraryName = libraryName; // allowed to be null

        if (operatorName == null || operatorName.equals("")) {
            throw new IllegalArgumentException("operatorName is null");
        }

        this.operatorName = operatorName;
        this.signature = new Signature(signature);
    }

    private String libraryName;
    public String getLibraryName() {
        return libraryName;
    }

    private String operatorName;
    public String getOperatorName() {
        return operatorName;
    }

    private Signature signature;
    public Signature getSignature() {
        return signature;
    }
}
