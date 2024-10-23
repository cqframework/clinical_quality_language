package org.hl7.cql.model;

public class WildcardType extends DataType {

    private String name = "?";

    public WildcardType() {
        super(null);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean isCompatibleWith(DataType otherType) {
        return true;
    }

    @Override
    public boolean isGeneric() {
        return false;
    }

    @Override
    public boolean isInstantiable(DataType callType, InstantiationContext context) {
        // NOTE: A wildcard type will never be in the generic signature, so this should never be called
        return false;
    }

    @Override
    public DataType instantiate(InstantiationContext context) {
        // NOTE: A wildcard type will never be in the generic signature, so this should never be called
        return null;
    }

    @Override
    public boolean matchWildcards(DataType operandType, ResolutionContext context) {
        if (context.matchWildcard(this, operandType)) {
            return true;
        }

        // This should never happen...
        throw new IllegalArgumentException("Wildcard is already matched");
    }

    @Override
    public DataType resolveWildcards(ResolutionContext context) {
        return context.resolveWildcard(this);
    }
}
