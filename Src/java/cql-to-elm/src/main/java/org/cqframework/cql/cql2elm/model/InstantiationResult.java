package org.cqframework.cql.cql2elm.model;

import java.util.Map;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.TypeParameter;

/**
 * Created by Bryn on 12/22/2016.
 */
public class InstantiationResult {
    public InstantiationResult(
            GenericOperator genericOperator,
            Operator operator,
            Map<TypeParameter, DataType> typeMap,
            int conversionScore) {
        if (genericOperator == null) {
            throw new IllegalArgumentException("genericOperator is required");
        }

        this.genericOperator = genericOperator;
        this.operator = operator;
        this.typeMap = typeMap;
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

    private Map<TypeParameter, DataType> typeMap;

    public Map<TypeParameter, DataType> getTypeMap() {
        return typeMap;
    }

    private int conversionScore;

    public int getConversionScore() {
        return conversionScore;
    }
}
