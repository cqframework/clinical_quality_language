package org.cqframework.cql.cql2elm.model;

import java.util.HashMap;
import java.util.Map;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.ResolutionContext;

public class ResolutionContextImpl implements ResolutionContext {

    public ResolutionContextImpl(Map<DataType, DataType> wildcardMap) {
        if (wildcardMap == null) {
            throw new IllegalArgumentException("wildcardMap is null");
        }
        this.wildcardMap = wildcardMap;
        this.resolvedMap = new HashMap<DataType, DataType>();
        this.matchMap = new HashMap<DataType, Boolean>();
    }

    private Map<DataType, DataType> wildcardMap;
    private Map<DataType, DataType> resolvedMap;

    private Map<DataType, Boolean> matchMap;

    private void checkWildcardType(DataType wildcardType) {
        if (wildcardType == null) {
            throw new IllegalArgumentException("Type is required");
        }

        if (!wildcardType.isWildcard()) {
            throw new IllegalArgumentException(String.format("Type %s is not a wildcard", wildcardType));
        }
    }

    @Override
    public boolean matchWildcard(DataType wildcardType, DataType operandType) {
        checkWildcardType(wildcardType);

        DataType boundType = wildcardMap.get(wildcardType);
        if (boundType == null) {
            boundType = operandType;
            wildcardMap.put(wildcardType, boundType);
            return true;
        }

        if (boundType.equals(operandType)) {
            return true;
        }

        return false;
    }

    @Override
    public DataType resolveWildcard(DataType wildcardType) {
        checkWildcardType(wildcardType);
        DataType result = wildcardMap.get(wildcardType);
        if (result == null) {
            // If the wildcard was not matched to any type in the instantiation, treat it as an Any
            result = DataType.ANY;
        }

        return result;
    }

    @Override
    public DataType getResolvedType(DataType callType) {
        return resolvedMap.get(callType);
    }

    @Override
    public void putResolvedType(DataType callType, DataType resolvedType) {
        resolvedMap.put(callType, resolvedType);
    }

    @Override
    public Boolean getMatchResult(DataType callType) {
        return matchMap.get(callType);
    }

    @Override
    public void putMatchResult(DataType callType, Boolean matchResult) {
        matchMap.put(callType, matchResult);
    }
}
