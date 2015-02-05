package org.cqframework.cql.elm.tracking;

import org.hl7.elm_modelinfo.r1.TypeInfo;

import java.util.Map;

public abstract class DataType {
    private DataType baseType;

    public DataType() {

    }

    public DataType(DataType baseType) {
        setBaseType(baseType);
    }

    public DataType getBaseType() {
        return baseType;
    }

    public void setBaseType(DataType baseType) {
        this.baseType = baseType;
    }

    public boolean isSubTypeOf(DataType other) {
        DataType currentType = this;
        while (currentType != null) {
            if (currentType.equals(other)) {
                return true;
            }
            currentType = currentType.baseType;
        }

        return false;
    }

    public boolean isSuperTypeOf(DataType other) {
        while (other != null) {
            if (equals(other)) {
                return true;
            }
            other = other.baseType;
        }

        return false;
    }

    public abstract boolean isGeneric();

    public abstract boolean isInstantiable(DataType callType, Map<TypeParameter, DataType> typeMap);

    public abstract DataType instantiate(Map<TypeParameter, DataType> typeMap);
}
