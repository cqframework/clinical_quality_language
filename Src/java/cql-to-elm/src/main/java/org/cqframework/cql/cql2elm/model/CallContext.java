package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;

import java.util.ArrayList;
import java.util.List;

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

    private Operator operator;
    public Operator getOperator() {
        return operator;
    }
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    private List<Operator> conversions;
    public Iterable<Operator> getConversions() {
        return conversions;
    }
    public void setConversions(Iterable<Operator> conversions) {
        this.conversions.clear();
        for (Operator conversion : conversions) {
            this.conversions.add(conversion);
        }
    }

    public void setConversions(Operator[] conversions) {
        if (this.conversions == null) {
            this.conversions = new ArrayList<>();
        }
        else {
            this.conversions.clear();
        }

        for (int i = 0; i < conversions.length; i++) {
            this.conversions.add(conversions[i]);
        }
    }
}
