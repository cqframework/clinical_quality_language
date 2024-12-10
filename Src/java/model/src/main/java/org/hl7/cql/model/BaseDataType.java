package org.hl7.cql.model;

public abstract class BaseDataType implements DataType {
    protected BaseDataType() {
        this(DataType.ANY);
    }

    protected BaseDataType(DataType baseType) {
        this.baseType = baseType != null ? baseType : DataType.ANY;
    }

    private final DataType baseType;

    @Override
    public DataType getBaseType() {
        return baseType;
    }

    @Override
    public String toLabel() {
        return toString();
    }

    @Override
    public boolean isSubTypeOf(DataType other) {
        DataType currentType = this;
        while (currentType != null) {
            if (currentType.equals(other)) {
                return true;
            }
            currentType = currentType.getBaseType();
        }

        return false;
    }

    @Override
    public boolean isSuperTypeOf(DataType other) {
        while (other != null) {
            if (equals(other)) {
                return true;
            }
            other = other.getBaseType();
        }

        return false;
    }

    /**
     * @param other
     * @return The first supertype of this type that is also a supertype of other
     */
    @Override
    public DataType getCommonSuperTypeOf(DataType other) {
        DataType currentType = this;
        while (currentType != ANY) {
            if (currentType.isSuperTypeOf(other)) {
                return currentType;
            }
            currentType = currentType.getBaseType();
        }

        return ANY;
    }

    // Note that this is not how implicit/explicit conversions are defined, the notion of
    // type compatibility is used to support implicit casting, such as casting a "null"
    // literal to any other type, or casting a class to an equivalent tuple.
    @Override
    public boolean isCompatibleWith(DataType other) {
        // A type is compatible with a choice type if it is a subtype of one of the choice types
        if (other instanceof ChoiceType) {
            for (DataType choice : ((ChoiceType) other).getTypes()) {
                if (this.isSubTypeOf(choice)) {
                    return true;
                }
            }
        }

        return this.equals(other); // Any data type is compatible with itself
    }
}
