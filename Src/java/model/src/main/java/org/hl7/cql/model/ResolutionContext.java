package org.hl7.cql.model;

public interface ResolutionContext {

    boolean matchWildcard(WildcardType wildcardType, DataType operandType);

    DataType resolveWildcard(WildcardType wildcardType);
}
