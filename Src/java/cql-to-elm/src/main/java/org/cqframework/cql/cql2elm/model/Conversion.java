package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;

public class Conversion {
    public Conversion(Operator operator, boolean isImplicit) {
        setIsImplicit(isImplicit);
        setOperator(operator);
    }

    public Conversion(DataType fromType, DataType toType) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
    }

    private boolean implicit;
    public boolean isImplicit() {
        return implicit;
    }
    public void setIsImplicit(boolean implicit) {
        this.implicit = implicit;
    }

    private Operator operator;
    public Operator getOperator() {
        return operator;
    }
    public void setOperator(Operator operator) {
        if (operator == null) {
            throw new IllegalArgumentException("operator is null");
        }

        if (operator instanceof GenericOperator) {
            throw new IllegalArgumentException("Generic conversion operators are not supported.");
        }

        fromType = null;
        for (DataType dataType : operator.getSignature().getOperandTypes()) {
            if (fromType != null) {
                throw new IllegalArgumentException("Conversion operator must be unary.");
            }

            fromType = dataType;
        }

        if (fromType == null) {
            throw new IllegalArgumentException("Conversion operator must be unary.");
        }

        toType = operator.getResultType();

        this.operator = operator;
    }

    public boolean isCast() {
        return operator == null;
    }

    private DataType fromType;
    public DataType getFromType() {
        return fromType;
    }

    private DataType toType;
    public DataType getToType() {
        return toType;
    }
}
