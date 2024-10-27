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
        // A wildcard is always instantiable as at least Any
        return true;
    }

    @Override
    public DataType instantiate(InstantiationContext context) {
        return DataType.ANY;
    }

    @Override
    public boolean isWildcard() {
        return true;
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
