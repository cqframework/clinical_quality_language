package org.cqframework.cql.elm.tracking;

import java.util.Map;

public class TypeParameter extends DataType {
    public TypeParameter(String identifier) {
        if (identifier == null || identifier.equals("")) {
            throw new IllegalArgumentException("identifier is null");
        }

        this.identifier = identifier;
    }

    private String identifier;
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object o ) {
        if (o instanceof TypeParameter) {
            TypeParameter that = (TypeParameter)o;
            return this.identifier.equals(that.identifier);
        }

        return false;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public boolean isInstantiable(DataType callType, Map<TypeParameter, DataType> typeMap) {
        DataType boundType = typeMap.get(this);
        if (boundType == null) {
            typeMap.put(this, callType);
            return true;
        }
        else {
            return boundType.isSuperTypeOf(callType);
        }
    }

    @Override
    public DataType instantiate(Map<TypeParameter, DataType> typeMap) {
        DataType result = typeMap.get(this);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not resolve type parameter %s.", this.identifier));
        }

        return result;
    }
}
