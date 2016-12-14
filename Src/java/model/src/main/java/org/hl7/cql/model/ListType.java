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
            ListType that = (ListType)o;
            return this.elementType.equals(that.elementType);
        }

        return false;
    }

    @Override
    public boolean isSubTypeOf(DataType other) {
        if (other instanceof ListType) {
            ListType that = (ListType)other;
            return this.elementType.isSubTypeOf(that.elementType);
        }

        return super.isSubTypeOf(other);
    }

    @Override
    public boolean isSuperTypeOf(DataType other) {
        if (other instanceof ListType) {
            ListType that = (ListType)other;
            return this.elementType.isSuperTypeOf(that.elementType);
        }

        return super.isSuperTypeOf(other);
    }

    @Override
    public String toString() {
        return String.format("list<%s>", elementType.toString());
    }

    @Override
    public boolean isGeneric() {
        return elementType.isGeneric();
    }

    public boolean isInstantiable(DataType callType, InstantiationContext context) {
        if (callType instanceof ListType) {
            ListType listType = (ListType)callType;
            return elementType.isInstantiable(listType.elementType, context);
        }

        boolean isAlreadyInstantiable = false;
        for (ListType targetListType : context.getListConversionTargets(callType)) {
            boolean isInstantiable = elementType.isInstantiable(targetListType.elementType, context);
            if (isInstantiable) {
                if (isAlreadyInstantiable) {
                    throw new IllegalArgumentException(String.format("Ambiguous generic instantiation involving %s to %s.",
                            callType.toString(), targetListType.toString()));
                }
                isAlreadyInstantiable = true;
            }
        }

        if (isAlreadyInstantiable) {
            return true;
        }

        return false;
    }

    @Override
    public DataType instantiate(InstantiationContext context) {
        return new ListType(elementType.instantiate(context));
    }
}
