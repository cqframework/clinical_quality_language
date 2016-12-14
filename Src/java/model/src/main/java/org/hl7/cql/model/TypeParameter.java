package org.hl7.cql.model;

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
    public boolean isInstantiable(DataType callType, InstantiationContext context) {
        return context.isInstantiable(this, callType);
    }

    @Override
    public DataType instantiate(InstantiationContext context) {
        return context.instantiate(this);
    }
}
