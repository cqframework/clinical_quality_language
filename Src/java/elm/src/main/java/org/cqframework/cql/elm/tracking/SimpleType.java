package org.cqframework.cql.elm.tracking;

import java.util.Map;

public class SimpleType extends DataType implements NamedType {
    private String name;

    public SimpleType(String name, DataType baseType) {
        super(baseType);

        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name");
        }
        this.name = name;
    }

    public SimpleType(String name) {
        this(name, null);
    }

    public String getNamespace() {
        int qualifierIndex = this.name.indexOf('.');
        if (qualifierIndex > 0) {
            return this.name.substring(0, qualifierIndex);
        }

        return "";
    }

    public String getSimpleName() {
        int qualifierIndex = this.name.indexOf('.');
        if (qualifierIndex > 0) {
            return this.name.substring(qualifierIndex + 1);
        }

        return this.name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimpleType) {
            SimpleType that = (SimpleType)o;
            return this.name.equals(that.name);
        }

        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean isGeneric() {
        return false;
    }

    @Override
    public boolean isInstantiable(DataType callType, Map<TypeParameter, DataType> typeMap) {
        return isSuperTypeOf(callType);
    }

    @Override
    public DataType instantiate(Map<TypeParameter, DataType> typeMap) {
        return this;
    }
}
