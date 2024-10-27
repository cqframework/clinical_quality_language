package org.hl7.cql.model;

public interface ResolutionContext {

    boolean matchWildcard(DataType wildcardType, DataType operandType);

    DataType resolveWildcard(DataType wildcardType);

    DataType getResolvedType(DataType callType);

    void putResolvedType(DataType callType, DataType resolvedType);

    Boolean getMatchResult(DataType callType);

    void putMatchResult(DataType callType, Boolean matchResult);
}
