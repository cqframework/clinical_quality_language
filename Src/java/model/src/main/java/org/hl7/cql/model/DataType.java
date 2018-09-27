package org.hl7.cql.model;

public abstract class DataType {
    public DataType() {
        this(null);
    }
    public DataType(DataType baseType) {
        this.baseType = baseType == null ? DataType.ANY : baseType;
    }

    private DataType baseType;
    public DataType getBaseType() {
        return baseType;
    }

    public String toLabel() {
        return toString();
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

    /**
     * @param other
     * @return The first supertype of this type that is also a supertype of other
     */
    public DataType getCommonSuperTypeOf(DataType other) {
        DataType currentType = this;
        while (currentType != null) {
            if (currentType.isSuperTypeOf(other)) {
                return currentType;
            }
            currentType = currentType.baseType;
        }

        return null;
    }

    // Note that this is not how implicit/explicit conversions are defined, the notion of
    // type compatibility is used to support implicit casting, such as casting a "null"
    // literal to any other type, or casting a class to an equivalent tuple.
    public boolean isCompatibleWith(DataType other) {
        // A type is compatible with a choice type if it is a subtype of one of the choice types
        if (other instanceof ChoiceType) {
            for (DataType choice : ((ChoiceType)other).getTypes()) {
                if (this.isSubTypeOf(choice)) {
                    return true;
                }
            }
        }

        return this.equals(other); // Any data type is compatible with itself
    }

    public abstract boolean isGeneric();

    public abstract boolean isInstantiable(DataType callType, InstantiationContext context);

    public abstract DataType instantiate(InstantiationContext context);

    public static final SimpleType ANY = new SimpleType("System.Any");
}
