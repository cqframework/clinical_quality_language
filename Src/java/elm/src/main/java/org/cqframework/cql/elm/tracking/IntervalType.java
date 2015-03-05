package org.cqframework.cql.elm.tracking;

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
    public boolean isSubTypeOf(DataType other) {
        if (other instanceof IntervalType) {
            IntervalType that = (IntervalType)other;
            return this.pointType.isSubTypeOf(that.pointType);
        }

        return super.isSubTypeOf(other);
    }

    @Override
    public boolean isSuperTypeOf(DataType other) {
        if (other instanceof IntervalType) {
            IntervalType that = (IntervalType)other;
            return this.pointType.isSuperTypeOf(that.pointType);
        }

        return super.isSuperTypeOf(other);
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
    public boolean isInstantiable(DataType callType, InstantiationContext context) {
        if (callType instanceof IntervalType) {
            IntervalType intervalType = (IntervalType)callType;
            return pointType.isInstantiable(intervalType.pointType, context);
        }

        return false;
    }

    @Override
    public DataType instantiate(InstantiationContext context) {
        return new IntervalType(pointType.instantiate(context));
    }
}
