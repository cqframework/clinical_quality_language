package org.hl7.cql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bryn on 11/8/2016.
 */
public class ChoiceType extends DataType {

    public ChoiceType(Iterable<DataType> types) {
        // Expand choice types in the constructor, it never makes sense to have a choice of choices
        for (DataType type : types) {
            addType(type);
        }
    }

    private ArrayList<DataType> types = new ArrayList<>();
    public Iterable<DataType> getTypes() {
        return types;
    }

    private void addType(DataType type) {
        if (type instanceof ChoiceType) {
            ChoiceType choiceType = (ChoiceType)type;
            for (DataType choice : choiceType.getTypes()) {
                addType(choice);
            }
        }
        else {
            types.add(type);
        }
    }

    @Override
    public int hashCode() {
        int result = 13;
        for (int i = 0; i < types.size(); i++) {
            result += (37 * types.get(i).hashCode());
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChoiceType) {
            ChoiceType that = (ChoiceType)o;

            if (this.types.size() == that.types.size()) {
                List<DataType> theseTypes = this.types;
                List<DataType> thoseTypes = that.types;
                for (int i = 0; i < theseTypes.size(); i++) {
                    if (!theseTypes.get(i).equals(thoseTypes.get(i))) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSubTypeOf(DataType other) {
        if (other instanceof ChoiceType) {
	        // TODO: Determine isSubTypeOf semantics for choice types
	        // A choice type A is a subtype of a choice type B if each component type is a subtype of some copmonent type of B // Holding off on this more complex case for now
	        for (DataType alternative: this.types) {
	            if (! alternative.isSubTypeOf( other )) {
	            	return false;
	            }
	        }
	        return true;
        } else {
	        // 'Other' is a single (non choice) type.
	        // This is a sub-type if all the alternatives are sub-types
	        for (DataType alternative: this.types) {
		        if ( ! alternative.isSubTypeOf( other ) ) {
			        return false;
		        }
	        }
	        return true;
        }
    }

    @Override
    public boolean isSuperTypeOf(DataType other) {
        if (other instanceof ChoiceType) {
	        // TODO: Determine isSuperTypeOf semantics for choice types
	        // A choice type A is a supertype of a choice type B if B is a subtype of A
	        return other.isSubTypeOf( this );
        } else {
        	// 'Other' is a single (non choice) type.
	        // This is a super-type if at least one of the alternatives is a super-type
        	for (DataType alternative: this.types) {
        		if ( alternative.isSuperTypeOf( other ) ) {
        			return true;
		        }
	        }
	        return false;
        }
    }

    @Override
    public boolean isCompatibleWith(DataType other) {
        // This type is compatible with the other type if
            // The other type is a subtype of one of the choice types
            // The other type is a choice type and all its component types are a subtype of some component of this choice type // Holding off on this more complex case for now...
        for (DataType type : types) {
            if (other.isSubTypeOf(type)) {
                return true;
            }
        }

        return super.isCompatibleWith(other);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("choice<");
        boolean first = true;
        for (DataType type : types) {
            if (first) {
                first = false;
            }
            else {
                sb.append(",");
            }
            sb.append(type.toString());
        }
        sb.append(">");
        return sb.toString();
    }

    @Override
    public boolean isGeneric() {
        // TODO: It hardly makes sense for a choice type to have generics.... ignoring in instantiation semantics for now
        for (DataType type : types) {
            if (type.isGeneric()) {
                return true;
            }
        }

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
