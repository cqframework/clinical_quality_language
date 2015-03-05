package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversionMap {
    private Map<DataType, List<Conversion>> map = new HashMap<>();

    public void add(Conversion conversion) {
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

    public Conversion findCompatibleConversion(DataType fromType, DataType toType) {
        if (fromType.isCompatibleWith(toType)) {
            return new Conversion(fromType, toType);
        }

        return null;
    }

    public Conversion findConversion(DataType fromType, DataType toType, boolean isImplicit) {
        Conversion result = findCompatibleConversion(fromType, toType);
        if (result == null) {
            int score = Integer.MAX_VALUE;
            for (Conversion conversion : getConversions(fromType)) {
                if ((!isImplicit || conversion.isImplicit()) && (conversion.getToType().isSuperTypeOf(toType) || conversion.getToType().isGeneric())) {
                    // Lower score is better. If the conversion matches the target type exactly, the score is 0.
                    // If the conversion is generic, the score is 1 (because that will be instantiated to an exact match)
                    // If the conversion is a super type, it should only be used if an exact match cannot be found.
                    // If the score is equal to an existing, it indicates a duplicate conversion
                    int newScore = conversion.getToType().equals(toType) ? 0 : (conversion.getToType().isGeneric() ? 1 : 2);
                    if (newScore < score) {
                        result = conversion;
                        score = newScore;
                    }
                    else if (newScore == score) {
                        throw new IllegalArgumentException(String.format("Ambiguous implicit conversion from %s to %s or %s.",
                                fromType.toString(), result.getToType().toString(), conversion.getToType().toString()));
                    }
                }
            }
        }

        return result;
    }
}
