package org.hl7.cql.model;

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
            IntervalType that = (IntervalType) o;
            return this.pointType.equals(that.pointType);
        }

        return false;
    }

    @Override
    public boolean isSubTypeOf(DataType other) {
        if (other instanceof IntervalType) {
            IntervalType that = (IntervalType) other;
            return this.pointType.isSubTypeOf(that.pointType);
        }

        return super.isSubTypeOf(other);
    }

    @Override
    public boolean isSuperTypeOf(DataType other) {
        if (other instanceof IntervalType) {
            IntervalType that = (IntervalType) other;
            return this.pointType.isSuperTypeOf(that.pointType);
        }

        return super.isSuperTypeOf(other);
    }

    @Override
    public String toString() {
        return String.format("interval<%s>", pointType.toString());
    }

    @Override
    public String toLabel() {
        return String.format("Interval of %s", pointType.toLabel());
    }

    @Override
    public boolean isGeneric() {
        return pointType.isGeneric();
    }

    @Override
    public DataType instantiate(DataType callType, InstantiationContext context) {
        if (!isGeneric()) {
            return this;
        }

        if (callType == null) {
            var pointTypeInstantiated = pointType.instantiate(null, context);
            if (pointTypeInstantiated == null) {
                return null;
            }
            return new IntervalType(pointTypeInstantiated);
        }

        if (callType.equals(DataType.ANY)) {
            var pointTypeInstantiated = pointType.instantiate(DataType.ANY, context);
            if (pointTypeInstantiated == null) {
                return null;
            }
            return new IntervalType(pointTypeInstantiated);
        }

        if (callType instanceof IntervalType) {
            IntervalType intervalType = (IntervalType) callType;
            var pointTypeInstantiated = pointType.instantiate(intervalType.pointType, context);
            if (pointTypeInstantiated == null) {
                return null;
            }
            return new IntervalType(pointTypeInstantiated);
        }

        DataType pointTypeInstantiated = null;
        for (IntervalType targetIntervalType : context.getIntervalConversionTargets(callType)) {
            var pointTypeInstantiatedWithTargetPointType = pointType.instantiate(targetIntervalType.pointType, context);
            if (pointTypeInstantiatedWithTargetPointType != null) {
                if (pointTypeInstantiated != null) {
                    throw new IllegalArgumentException(String.format(
                            "Ambiguous generic instantiation involving %s to %s.",
                            callType.toString(), targetIntervalType.toString()));
                }
                pointTypeInstantiated = pointTypeInstantiatedWithTargetPointType;
            }
        }

        if (pointTypeInstantiated != null) {
            return new IntervalType(pointTypeInstantiated);
        }

        return null;
    }
}
