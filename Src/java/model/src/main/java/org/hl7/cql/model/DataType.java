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
        if ( other instanceof ChoiceType ) {
        	return other.isSuperTypeOf( this );
        } else {
	        return subTypeOf( other );
        }
    }

    private boolean subTypeOf(DataType other) {
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
	    if ( other instanceof ChoiceType ) {
		    return other.isSubTypeOf( this );
	    } else {
		    return superTypeOf( other );
	    }
    }

    private boolean superTypeOf(DataType other) {
        while (other != null) {
            if (equals(other)) {
                return true;
            }
            other = other.baseType;
        }

        return false;
    }

    // Note that this is not how implicit/explicit conversions are defined, the notion of
    // type compatibility is used to support implicit casting, such as casting a "null"
    // literal to any other type, or casting a class to an equivalent tuple.
    public boolean isCompatibleWith(DataType other) {
        return false; // default implementation returns false.
    }

    public abstract boolean isGeneric();

    public abstract boolean isInstantiable(DataType callType, InstantiationContext context);

    public abstract DataType instantiate(InstantiationContext context);

    public static final SimpleType ANY = new SimpleType("System.Any");
}
