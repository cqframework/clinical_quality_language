package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;
import org.cqframework.cql.elm.tracking.InstantiationContext;
import org.cqframework.cql.elm.tracking.TypeParameter;

import java.util.Map;

public class InstantiationContextImpl implements InstantiationContext {
    public InstantiationContextImpl(Map<TypeParameter, DataType> typeMap, ConversionMap conversionMap) {
        if (typeMap == null) {
            throw new IllegalArgumentException("typeMap is null");
        }

        if (conversionMap == null) {
            throw new IllegalArgumentException("conversionMap is null");
        }

        this.typeMap = typeMap;
        this.conversionMap = conversionMap;
    }

    private Map<TypeParameter, DataType> typeMap;
    private ConversionMap conversionMap;

    @Override
    public boolean isInstantiable(TypeParameter parameter, DataType callType) {
        // If the type is not yet bound, bind it to the call type.
        DataType boundType = typeMap.get(parameter);
        if (boundType == null) {
            typeMap.put(parameter, callType);
            return true;
        }
        else {
            // If the type is bound, and is a super type of the call type, return true;
            if (boundType.isSuperTypeOf(callType) || callType.isCompatibleWith(boundType)) {
                return true;
            }
            else if (callType.isSuperTypeOf(boundType) || boundType.isCompatibleWith(callType)) {
                // If the call type is a super type of the bound type, switch the bound type for this parameter to the call type
                typeMap.put(parameter, callType);
                return true;
            }
            else {
                // If there is an implicit conversion path from the call type to the bound type, return true
                Conversion conversion = conversionMap.findConversion(callType, boundType, true);
                if (conversion != null) {
                    return true;
                }

                // If there is an implicit conversion path from the bound type to the call type
                conversion = conversionMap.findConversion(boundType, callType, true);
                if (conversion != null) {
                    // switch the bound type to the call type and return true
                    typeMap.put(parameter, callType);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public DataType instantiate(TypeParameter parameter) {
        DataType result = typeMap.get(parameter);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not resolve type parameter %s.", parameter.getIdentifier()));
        }

        return result;
    }
}
