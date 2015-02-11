package org.cqframework.cql.elm.tracking;

import java.util.Map;

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

    public boolean isInstantiable(DataType callType, Map<TypeParameter, DataType> typeMap) {
        if (callType instanceof ListType) {
            ListType listType = (ListType)callType;
            return elementType.isInstantiable(listType.elementType, typeMap);
        }

        return false;
    }

    @Override
    public DataType instantiate(Map<TypeParameter, DataType> typeMap) {
        return new ListType(elementType.instantiate(typeMap));
    }
}
