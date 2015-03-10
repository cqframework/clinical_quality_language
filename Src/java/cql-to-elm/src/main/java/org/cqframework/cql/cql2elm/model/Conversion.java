package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;
import org.cqframework.cql.elm.tracking.IntervalType;
import org.cqframework.cql.elm.tracking.ListType;

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
        this.isCastFlag = true;
    }

    public Conversion(ListType fromType, ListType toType, Conversion elementConversion) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        if (elementConversion == null) {
            throw new IllegalArgumentException("elementConversion is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.conversionField = elementConversion;
        this.isListConversionFlag = true;
    }

    public Conversion(IntervalType fromType, IntervalType toType, Conversion pointConversion) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        if (pointConversion == null) {
            throw new IllegalArgumentException("pointConversion is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.conversionField = pointConversion;
        this.isIntervalConversionFlag = true;
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

    private Conversion conversionField;
    public Conversion getConversion() {
        return conversionField;
    }

    private boolean isCastFlag;
    public boolean isCast() {
        return isCastFlag;
    }

    private boolean isListConversionFlag;
    public boolean isListConversion() {
        return isListConversionFlag;
    }

    private boolean isIntervalConversionFlag;
    public boolean isIntervalConversion() {
        return isIntervalConversionFlag;
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
