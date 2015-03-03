package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;

public class Conversion {
    public Conversion(Operator operator, boolean isImplicit) {
        setIsImplicit(isImplicit);
        setOperator(operator);
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

    private DataType fromType;
    public DataType getFromType() {
        return fromType;
    }

    private DataType toType;
    public DataType getToType() {
        return toType;
    }
}
