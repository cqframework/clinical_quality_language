package org.cqframework.cql.cql2elm.model;


/**
 * Created by Bryn on 12/22/2016.
 */
public class InstantiationResult {
    public InstantiationResult(GenericOperator genericOperator, Operator operator, int conversionScore) {
        if (genericOperator == null) {
            throw new IllegalArgumentException("genericOperator is required");
        }

        this.genericOperator = genericOperator;
        this.operator = operator;
        this.conversionScore = conversionScore;
    }

    private GenericOperator genericOperator;
    public GenericOperator getGenericOperator() {
        return genericOperator;
    }

    private Operator operator;
    public Operator getOperator() {
        return operator;
    }

    private int conversionScore;
    public int getConversionScore() {
        return conversionScore;
    }
}
