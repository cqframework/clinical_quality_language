package org.hl7.cql.model;

public class ListType extends DataType {
    private DataType elementType;

    public ListType(DataType elementType) {
        super();

        if (elementType == null) {
            throw new IllegalArgumentException("elementType");
        }

        this.elementType = elementType;
    }

    public DataType getElementType() {
        return this.elementType;
    }

    @Override
    public int hashCode() {
        return 67 * elementType.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ListType) {
            ListType that = (ListType) o;
            return this.elementType.equals(that.elementType);
        }

        return false;
    }

    @Override
    public boolean isSubTypeOf(DataType other) {
        if (other instanceof ListType) {
            ListType that = (ListType) other;
            return this.elementType.isSubTypeOf(that.elementType);
        }

        return super.isSubTypeOf(other);
    }

    @Override
    public boolean isSuperTypeOf(DataType other) {
        if (other instanceof ListType) {
            ListType that = (ListType) other;
            return this.elementType.isSuperTypeOf(that.elementType);
        }

        return super.isSuperTypeOf(other);
    }

    @Override
    public String toString() {
        return String.format("list<%s>", elementType.toString());
    }

    @Override
    public String toLabel() {
        return String.format("List of %s", elementType.toLabel());
    }

    @Override
    public boolean isGeneric() {
        return elementType.isGeneric();
    }

    @Override
    public DataType instantiate(DataType callType, InstantiationContext context) {
        if (!isGeneric()) {
            return this;
        }

        if (callType == null) {
            var elementTypeInstantiated = elementType.instantiate(null, context);
            if (elementTypeInstantiated == null) {
                return null;
            }
            return new ListType(elementTypeInstantiated);
        }

        if (callType.equals(DataType.ANY)) {
            var elementTypeInstantiated = elementType.instantiate(DataType.ANY, context);
            if (elementTypeInstantiated == null) {
                return null;
            }
            return new ListType(elementTypeInstantiated);
        }

        if (callType instanceof ListType) {
            ListType listType = (ListType) callType;
            var elementTypeInstantiated = elementType.instantiate(listType.elementType, context);
            if (elementTypeInstantiated == null) {
                return null;
            }
            return new ListType(elementTypeInstantiated);
        }

        DataType elementTypeInstantiated = null;
        for (ListType targetListType : context.getListConversionTargets(callType)) {
            var elementTypeInstantiatedWithTargetElementType =
                    elementType.instantiate(targetListType.elementType, context);
            if (elementTypeInstantiatedWithTargetElementType != null) {
                if (elementTypeInstantiated != null) {
                    throw new IllegalArgumentException(String.format(
                            "Ambiguous generic instantiation involving %s to %s.",
                            callType.toString(), targetListType.toString()));
                }
                elementTypeInstantiated = elementTypeInstantiatedWithTargetElementType;
            }
        }

        if (elementTypeInstantiated != null) {
            return new ListType(elementTypeInstantiated);
        }

        return null;
    }
}
