package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversionMap {
    private Map<DataType, List<Conversion>> map = new HashMap<>();

    private void add(Conversion conversion) {
        if (conversion == null) {
            throw new IllegalArgumentException("conversion is null.");
        }

        List<Conversion> conversions = getConversions(conversion.getFromType());
        if (conversions.contains(conversion)) {
            throw new IllegalArgumentException(String.format("Conversion from %s to %s is already defined.",
                    conversion.getFromType().toString(), conversion.getToType().toString()));
        }

        conversions.add(conversion);
    }

    private List<Conversion> getConversions(DataType fromType) {
        List<Conversion> conversions = map.get(fromType);
        if (conversions == null) {
            conversions = new ArrayList<Conversion>();
            map.put(fromType, conversions);
        }

        return conversions;
    }

    public Conversion findConversion(DataType fromType, DataType toType, boolean isImplicit) {
        Conversion result = null;
        for (Conversion conversion : getConversions(fromType)) {
            if ((!isImplicit || conversion.isImplicit()) && conversion.getToType().isSuperTypeOf(toType)) {
                if (result != null) {
                    throw new IllegalArgumentException(String.format("Ambiguous implicit conversion from %s to %s or %s.",
                            fromType.toString(), result.getToType().toString(), conversion.getToType().toString()));
                }

                result = conversion;
            }
        }

        return result;
    }
}
