package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;

public class CallContext {
    public CallContext(String libraryName, String operatorName, boolean allowPromotionAndDemotion, boolean mustResolve, DataType... signature) {
        this.libraryName = libraryName; // allowed to be null

        if (operatorName == null || operatorName.equals("")) {
            throw new IllegalArgumentException("operatorName is null");
        }

        this.operatorName = operatorName;
        this.signature = new Signature(signature);
        this.allowPromotionAndDemotion = allowPromotionAndDemotion;
        this.mustResolve = mustResolve;
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

    private boolean allowPromotionAndDemotion;
    public boolean getAllowPromotionAndDemotion() {
        return allowPromotionAndDemotion;
    }

    private boolean mustResolve;
    public boolean getMustResolve() {
        return this.mustResolve;
    }

}
