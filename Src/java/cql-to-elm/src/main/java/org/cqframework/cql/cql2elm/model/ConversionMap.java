package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.ChoiceType;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.IntervalType;
import org.hl7.cql.model.ListType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversionMap {
    public enum ConversionScore {
        ExactMatch (0),
        SubType (1),
        Compatible (2),
        Cast (3),
        SimpleConversion (4),
        ComplexConversion (5),
        IntervalDemotion (6),
        ListDemotion (7),
        IntervalPromotion (8),
        ListPromotion (9);

        private final int score;
        public int score() {
            return score;
        }

        ConversionScore(int score) {
            this.score = score;
        }
    }
    private Map<DataType, List<Conversion>> map = new HashMap<>();
    private List<Conversion> genericConversions = new ArrayList<>();
    private boolean demotion = true;
    private boolean promotion = true;

    public void enableDemotion() {
        demotion = true;
    }

    public void disableDemotion() {
        demotion = false;
    }

    public void enablePromotion() {
        promotion = true;
    }

    public void disablePromotion() {
        promotion = false;
    }

    public void add(Conversion conversion) {
        if (conversion == null) {
            throw new IllegalArgumentException("conversion is null.");
        }

        // NOTE: The conversion map supports generic conversions, however, they turned out to be quite expensive computationally
        // so we introduced list promotion and demotion instead (we should add interval promotion and demotion too, would be quite useful)
        // Generic conversions could still be potentially useful, so I left the code, but it's never used because the generic conversions
        // are not added in the SystemLibraryHelper.
        if (conversion.isGeneric()) {
            List<Conversion> conversions = getGenericConversions();
            if (conversions.contains(conversion)) {
                throw new IllegalArgumentException(String.format("Conversion from %s to %s is already defined.",
                        conversion.getFromType().toString(), conversion.getToType().toString()));
            }

            conversions.add(conversion);
        }
        else {
            List<Conversion> conversions = getConversions(conversion.getFromType());
            if (conversions.contains(conversion)) {
                throw new IllegalArgumentException(String.format("Conversion from %s to %s is already defined.",
                        conversion.getFromType().toString(), conversion.getToType().toString()));
            }

            conversions.add(conversion);
        }

    }

    public List<Conversion> getGenericConversions() {
        return genericConversions;
    }

    public List<Conversion> getConversions(DataType fromType) {
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

    public Conversion findChoiceConversion(ChoiceType fromType, DataType toType, OperatorMap operatorMap) {
        Conversion result = null;
        for (DataType choice : fromType.getTypes()) {
            Conversion choiceConversion = findConversion(choice, toType, true, operatorMap);
            if (choiceConversion != null) {
                if (result == null) {
                    result = new Conversion(fromType, toType, choiceConversion);
                }
                else {
                    result.addAlternativeConversion(choiceConversion);
                }
            }
        }

        return result;
    }

    public Conversion findListConversion(ListType fromType, ListType toType, OperatorMap operatorMap) {
        Conversion elementConversion = findConversion(fromType.getElementType(), toType.getElementType(), true, operatorMap);

        if (elementConversion != null) {
            return new Conversion(fromType, toType, elementConversion);
        }

        return null;
    }

    public Conversion findIntervalConversion(IntervalType fromType, IntervalType toType, OperatorMap operatorMap) {
        Conversion pointConversion = findConversion(fromType.getPointType(), toType.getPointType(), true, operatorMap);

        if (pointConversion != null) {
            return new Conversion(fromType, toType, pointConversion);
        }

        return null;
    }

    public Conversion findListDemotion(ListType fromType, DataType toType, OperatorMap operatorMap) {
        DataType elementType = fromType.getElementType();
        if (elementType.isSubTypeOf(toType)) {
            return new Conversion(fromType, toType, null);
        }
        else {
            Conversion elementConversion = findConversion(elementType, toType, true, operatorMap);
            if (elementConversion != null) {
                return new Conversion(fromType, toType, elementConversion);
            }
        }

        return null;
    }

    public Conversion findListPromotion(DataType fromType, ListType toType, OperatorMap operatorMap) {
        if (fromType.isSubTypeOf(toType.getElementType())) {
            return new Conversion(fromType, toType, null);
        }
        else {
            Conversion elementConversion = findConversion(fromType, toType.getElementType(), true, operatorMap);
            if (elementConversion != null) {
                return new Conversion(fromType, toType, elementConversion);
            }
        }

        return null;
    }

    public Conversion findIntervalDemotion(IntervalType fromType, DataType toType, OperatorMap operatorMap) {
        DataType pointType = fromType.getPointType();
        if (pointType.isSubTypeOf(toType)) {
            return new Conversion(fromType, toType, null);
        }
        else {
            Conversion pointConversion = findConversion(pointType, toType, true, operatorMap);
            if (pointConversion != null) {
                return new Conversion(fromType, toType, pointConversion);
            }
        }

        return null;
    }

    public Conversion findIntervalPromotion(DataType fromType, IntervalType toType, OperatorMap operatorMap) {
        if (fromType.isSubTypeOf(toType.getPointType())) {
            return new Conversion(fromType, toType, null);
        }
        else {
            Conversion pointConversion = findConversion(fromType, toType.getPointType(), true, operatorMap);
            if (pointConversion != null) {
                return new Conversion(fromType, toType, pointConversion);
            }
        }

        return null;
    }

    public void ensureGenericConversionInstantiated(DataType fromType, DataType toType, boolean isImplicit, OperatorMap operatorMap) {
        for (Conversion c : getGenericConversions()) {
            if (c.getOperator() != null) {
                // instantiate the generic...
                InstantiationResult instantiationResult = ((GenericOperator)c.getOperator()).instantiate(new Signature(fromType), operatorMap, this);
                Operator operator = instantiationResult.getOperator();
                if (operator != null && !operatorMap.containsOperator(operator)) {
                    operatorMap.addOperator(operator);
                    Conversion conversion = new Conversion(operator, true);
                    this.add(conversion);
                }
            }
        }
    }

    private Conversion internalFindConversion(DataType fromType, DataType toType, boolean isImplicit) {
        Conversion result = null;
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

        return result;
    }

    public Conversion findConversion(DataType fromType, DataType toType, boolean isImplicit, OperatorMap operatorMap) {
        Conversion result = findCompatibleConversion(fromType, toType);
        if (result == null) {
            result = internalFindConversion(fromType, toType, isImplicit);
        }

        if (result == null) {
            ensureGenericConversionInstantiated(fromType, toType, isImplicit, operatorMap);
            result = internalFindConversion(fromType, toType, isImplicit);
        }

        if (result == null) {
            // NOTE: FHIRPath Implicit conversion from list to singleton
            // If the from type is a list and the target type is a singleton (potentially with a compatible conversion),
            // Convert by invoking a singleton
            if (fromType instanceof ListType && !(toType instanceof ListType) && demotion) {
                result = findListDemotion((ListType)fromType, toType, operatorMap);
            }

            if (!(fromType instanceof ListType) && toType instanceof ListType && promotion) {
                result = findListPromotion(fromType, (ListType)toType, operatorMap);
            }

            if (fromType instanceof IntervalType && !(toType instanceof IntervalType) && demotion) {
                result = findIntervalDemotion((IntervalType)fromType, toType, operatorMap);
            }

            if (!(fromType instanceof IntervalType) && toType instanceof IntervalType && promotion) {
                result = findIntervalPromotion(fromType, (IntervalType)toType, operatorMap);
            }

            // If the from type is a choice, attempt to find a conversion between one of the choice types
            if (fromType instanceof ChoiceType) {
                result = findChoiceConversion((ChoiceType)fromType, toType, operatorMap);
            }

            // If both types are lists, attempt to find a conversion between the element types
            if (fromType instanceof ListType && toType instanceof ListType) {
                result = findListConversion((ListType)fromType, (ListType)toType, operatorMap);
            }

            // If both types are intervals, attempt to find a conversion between the point types
            if (fromType instanceof IntervalType && toType instanceof IntervalType) {
                result = findIntervalConversion((IntervalType)fromType, (IntervalType)toType, operatorMap);
            }
        }

        return result;
    }
}
