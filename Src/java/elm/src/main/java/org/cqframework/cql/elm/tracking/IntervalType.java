package org.cqframework.cql.elm.tracking;

import java.util.Map;

public class IntervalType extends DataType {
    private DataType pointType;

    public IntervalType(DataType pointType) {
        super();

        if (pointType == null) {
            throw new IllegalArgumentException("pointType");
        }

        this.pointType = pointType;
    }

    public DataType getPointType() {
        return this.pointType;
    }

    @Override
    public int hashCode() {
        return 53 * pointType.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IntervalType) {
            IntervalType that = (IntervalType)o;
            return this.pointType.equals(that.pointType);
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("interval<%s>", pointType.toString());
    }

    @Override
    public boolean isGeneric() {
        return pointType.isGeneric();
    }

    @Override
    public boolean isInstantiable(DataType callType, Map<TypeParameter, DataType> typeMap) {
        if (callType instanceof IntervalType) {
            IntervalType intervalType = (IntervalType)callType;
            return pointType.isInstantiable(intervalType.pointType, typeMap);
        }

        return false;
    }

    @Override
    public DataType instantiate(Map<TypeParameter, DataType> typeMap) {
        return new IntervalType(pointType.instantiate(typeMap));
    }
}
