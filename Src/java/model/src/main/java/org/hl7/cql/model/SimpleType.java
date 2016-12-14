package org.hl7.cql.model;

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
    public boolean isCompatibleWith(DataType other) {
        // The system type "Any" can be implicitly cast to any other type.
        // Any data type is compatible with itself
        return this.equals(DataType.ANY) || this.equals(other);
    }

    @Override
    public boolean isGeneric() {
        return false;
    }

    @Override
    public boolean isInstantiable(DataType callType, InstantiationContext context) {
        return isSuperTypeOf(callType);
    }

    @Override
    public DataType instantiate(InstantiationContext context) {
        return this;
    }
}
