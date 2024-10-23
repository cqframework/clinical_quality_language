package org.cqframework.cql.cql2elm.model;

import java.util.Map;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.ResolutionContext;
import org.hl7.cql.model.WildcardType;

public class ResolutionContextImpl implements ResolutionContext {

    public ResolutionContextImpl(Map<WildcardType, DataType> wildcardMap) {
        if (wildcardMap == null) {
            throw new IllegalArgumentException("wildcardMap is null");
        }
        this.wildcardMap = wildcardMap;
    }

    private Map<WildcardType, DataType> wildcardMap;

    @Override
    public boolean matchWildcard(WildcardType wildcardType, DataType operandType) {
        DataType boundType = wildcardMap.get(wildcardType);
        if (boundType == null) {
            boundType = operandType;
            wildcardMap.put(wildcardType, boundType);
            return true;
        }

        return false;
    }

    @Override
    public DataType resolveWildcard(WildcardType wildcardType) {
        DataType result = wildcardMap.get(wildcardType);
        if (result == null) {
            throw new IllegalArgumentException(
                    String.format("Could not resolve wildcard parameter %s.", wildcardType.toString()));
        }

        return result;
    }
}
