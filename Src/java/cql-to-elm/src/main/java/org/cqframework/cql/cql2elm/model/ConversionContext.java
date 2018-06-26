package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;

/**
 * Created by Bryn on 6/25/2018.
 */
public class ConversionContext {

    public ConversionContext(DataType fromType, DataType toType, boolean isImplicit, OperatorMap operatorMap) {
        this.fromType = fromType;
        this.toType = toType;
        this.isImplicit = isImplicit;
        this.operatorMap = operatorMap;
    }

    private DataType fromType;
    public DataType getFromType() {
        return fromType;
    }

    private DataType toType;
    public DataType getToType() {
        return toType;
    }

    private boolean isImplicit;
    public boolean getIsImplicit() {
        return isImplicit;
    }

    private OperatorMap operatorMap;
    public OperatorMap getOperatorMap() {
        return operatorMap;
    }
}
