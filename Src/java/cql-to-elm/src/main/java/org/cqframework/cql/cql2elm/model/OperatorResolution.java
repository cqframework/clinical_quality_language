package org.cqframework.cql.cql2elm.model;

import java.util.ArrayList;
import java.util.List;

public class OperatorResolution {
    public OperatorResolution() {

    }

    public OperatorResolution(Operator operator) {
        this.operator = operator;
    }

    private Operator operator;
    public Operator getOperator() {
        return operator;
    }
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    private void ensureConversions() {
        if (this.conversions == null) {
            this.conversions = new ArrayList<>();
        }
        else {
            this.conversions.clear();
        }
    }

    private List<Conversion> conversions;
    public Iterable<Conversion> getConversions() {
        return conversions;
    }
    public void setConversions(Iterable<Conversion> conversions) {
        ensureConversions();
        for (Conversion conversion : conversions) {
            this.conversions.add(conversion);
        }
    }

    public void setConversions(Conversion[] conversions) {
        ensureConversions();
        for (int i = 0; i < conversions.length; i++) {
            this.conversions.add(conversions[i]);
        }
    }

    public boolean hasConversions() {
        return this.conversions != null;
    }
}
