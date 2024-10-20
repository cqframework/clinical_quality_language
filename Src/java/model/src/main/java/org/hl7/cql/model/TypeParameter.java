package org.hl7.cql.model;

public class TypeParameter extends DataType {
    public enum TypeParameterConstraint {
        /**
         * Indicates the type parameter has no constraint and be bound to any type
         */
        NONE,

        /**
         * Indicates the type parameter can only be bound to class types
         */
        CLASS,

        /**
         * Indicates the type parameter can only be bound to value types (simple types)
         */
        VALUE,

        /**
         * Indicates the type parameter can only be bound to tuple types
         */
        TUPLE,

        /**
         * Indicates the type parameter can only be bound to interval types
         */
        INTERVAL,

        /**
         * Indicates the type parameter can only be bound to choice types
         */
        CHOICE,

        /**
         * Indicates the type parameter can only be bound to the constraint type or a type derived from the constraint type
         */
        TYPE
    }

    public TypeParameter(String identifier) {
        if (identifier == null || identifier.equals("")) {
            throw new IllegalArgumentException("identifier is null");
        }

        this.identifier = identifier;
    }

    public TypeParameter(String identifier, TypeParameterConstraint constraint, DataType constraintType) {
        this(identifier);
        this.constraint = constraint;
        this.constraintType = constraintType;
    }

    private String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public TypeParameterConstraint constraint = TypeParameterConstraint.NONE;

    public TypeParameterConstraint getConstraint() {
        return constraint;
    }

    private DataType constraintType;

    public DataType getConstraintType() {
        return constraintType;
    }

    /**
     * @param callType
     * @return True if the given callType can be bound to this parameter (i.e. it satisfied any constraints defined for the type parameter)
     */
    public boolean canBind(DataType callType) {
        switch (constraint) {
            case CHOICE:
                return callType instanceof ChoiceType;
            case TUPLE:
                return callType instanceof TupleType;
            case INTERVAL:
                return callType instanceof IntervalType;
            case CLASS:
                return callType instanceof ClassType;
            case VALUE:
                return callType instanceof SimpleType && !callType.equals(DataType.ANY);
            case TYPE:
                return callType.isSubTypeOf(constraintType);
            case NONE:
            default:
                return true;
        }
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TypeParameter) {
            TypeParameter that = (TypeParameter) o;
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
    public DataType instantiate(DataType callType, InstantiationContext context) {
        return context.instantiate(this, callType);
    }
}
