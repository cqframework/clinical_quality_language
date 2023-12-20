package org.cqframework.cql.cql2elm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hl7.cql.model.*;

public class ConversionMap {
    public enum ConversionScore {
        ExactMatch(0),
        SubType(1),
        Compatible(2),
        Cast(3),
        SimpleConversion(4),
        ComplexConversion(5),
        IntervalPromotion(6),
        ListDemotion(7),
        IntervalDemotion(8),
        ListPromotion(9);

        private final int score;

        public int score() {
            return score;
        }

        ConversionScore(int score) {
            this.score = score;
        }
    }

    public static int getConversionScore(DataType callOperand, DataType operand, Conversion conversion) {
        if (operand.equals(callOperand)) {
            return ConversionMap.ConversionScore.ExactMatch.score();
        } else if (operand.isSuperTypeOf(callOperand)) {
            return ConversionMap.ConversionScore.SubType.score();
        } else if (callOperand.isCompatibleWith(operand)) {
            return ConversionMap.ConversionScore.Compatible.score();
        } else if (conversion != null) {
            return conversion.getScore();
        }

        throw new IllegalArgumentException("Could not determine conversion score for conversion");
    }

    private Map<DataType, List<Conversion>> map = new HashMap<>();
    private List<Conversion> genericConversions = new ArrayList<>();
    private boolean listDemotion = true;
    private boolean listPromotion = true;
    private boolean intervalDemotion = false;
    private boolean intervalPromotion = false;

    public void enableListDemotion() {
        listDemotion = true;
    }

    public void disableListDemotion() {
        listDemotion = false;
    }

    public boolean isListDemotionEnabled() {
        return listDemotion;
    }

    public void enableListPromotion() {
        listPromotion = true;
    }

    public void disableListPromotion() {
        listPromotion = false;
    }

    public boolean isListPromotionEnabled() {
        return listPromotion;
    }

    public void enableIntervalDemotion() {
        intervalDemotion = true;
    }

    public void disableIntervalDemotion() {
        intervalDemotion = false;
    }

    public boolean isIntervalDemotionEnabled() {
        return intervalDemotion;
    }

    public void enableIntervalPromotion() {
        intervalPromotion = true;
    }

    public void disableIntervalPromotion() {
        intervalPromotion = false;
    }

    public boolean isIntervalPromotionEnabled() {
        return intervalPromotion;
    }

    private boolean hasConversion(Conversion conversion, List<Conversion> conversions) {
        for (Conversion c : conversions) {
            if (conversion.getToType().equals(c.getToType())) {
                return true;
            }
        }

        return false;
    }

    public Operator getConversionOperator(DataType fromType, DataType toType) {
        for (Conversion c : this.getConversions(fromType)) {
            if (c.getToType().equals(toType)) {
                return c.getOperator();
            }
        }

        return null;
    }

    public void add(Conversion conversion) {
        if (conversion == null) {
            throw new IllegalArgumentException("conversion is null.");
        }

        // NOTE: The conversion map supports generic conversions, however, they turned out to be quite expensive
        // computationally
        // so we introduced list promotion and demotion instead (we should add interval promotion and demotion too,
        // would be quite useful)
        // Generic conversions could still be potentially useful, so I left the code, but it's never used because the
        // generic conversions
        // are not added in the SystemLibraryHelper.
        if (conversion.isGeneric()) {
            List<Conversion> conversions = getGenericConversions();
            if (hasConversion(conversion, conversions)) {
                throw new IllegalArgumentException(String.format(
                        "Conversion from %s to %s is already defined.",
                        conversion.getFromType().toString(),
                        conversion.getToType().toString()));
            }

            conversions.add(conversion);
        } else {
            List<Conversion> conversions = getConversions(conversion.getFromType());
            if (hasConversion(conversion, conversions)) {
                throw new IllegalArgumentException(String.format(
                        "Conversion from %s to %s is already defined.",
                        conversion.getFromType().toString(),
                        conversion.getToType().toString()));
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

    /*
    Returns conversions for the given type, or any supertype, recursively
     */
    public List<Conversion> getAllConversions(DataType fromType) {
        List<Conversion> conversions = new ArrayList<Conversion>();
        DataType currentType = fromType;
        while (currentType != null) {
            conversions.addAll(getConversions(currentType));
            currentType = currentType.getBaseType();
        }
        return conversions;
    }

    public Conversion findCompatibleConversion(DataType fromType, DataType toType) {
        if (fromType.isCompatibleWith(toType)) {
            return new Conversion(fromType, toType);
        }

        return null;
    }

    public Conversion findChoiceConversion(
            ChoiceType fromType, DataType toType, boolean allowPromotionAndDemotion, OperatorMap operatorMap) {
        Conversion result = null;
        for (DataType choice : fromType.getTypes()) {
            Conversion choiceConversion = findConversion(choice, toType, true, allowPromotionAndDemotion, operatorMap);
            if (choiceConversion != null) {
                if (result == null) {
                    result = new Conversion(fromType, toType, choiceConversion);
                } else {
                    result.addAlternativeConversion(choiceConversion);
                }
            }
        }

        return result;
    }

    public Conversion findTargetChoiceConversion(
            DataType fromType, ChoiceType toType, boolean allowPromotionAndDemotion, OperatorMap operatorMap) {
        for (DataType choice : toType.getTypes()) {
            Conversion choiceConversion =
                    findConversion(fromType, choice, true, allowPromotionAndDemotion, operatorMap);
            if (choiceConversion != null) {
                return new Conversion(fromType, toType, choiceConversion);
            }
        }

        return null;
    }

    public Conversion findListConversion(ListType fromType, ListType toType, OperatorMap operatorMap) {
        Conversion elementConversion =
                findConversion(fromType.getElementType(), toType.getElementType(), true, false, operatorMap);

        if (elementConversion != null) {
            return new Conversion(fromType, toType, elementConversion);
        }

        return null;
    }

    public Conversion findIntervalConversion(IntervalType fromType, IntervalType toType, OperatorMap operatorMap) {
        Conversion pointConversion =
                findConversion(fromType.getPointType(), toType.getPointType(), true, false, operatorMap);

        if (pointConversion != null) {
            return new Conversion(fromType, toType, pointConversion);
        }

        return null;
    }

    public Conversion findListDemotion(ListType fromType, DataType toType, OperatorMap operatorMap) {
        DataType elementType = fromType.getElementType();
        if (elementType.isSubTypeOf(toType)) {
            return new Conversion(fromType, toType, null);
        } else {
            Conversion elementConversion = findConversion(elementType, toType, true, false, operatorMap);
            if (elementConversion != null) {
                return new Conversion(fromType, toType, elementConversion);
            }
        }

        return null;
    }

    public Conversion findListPromotion(DataType fromType, ListType toType, OperatorMap operatorMap) {
        if (fromType.isSubTypeOf(toType.getElementType())) {
            return new Conversion(fromType, toType, null);
        } else {
            Conversion elementConversion = findConversion(fromType, toType.getElementType(), true, false, operatorMap);
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
        } else {
            Conversion pointConversion = findConversion(pointType, toType, true, false, operatorMap);
            if (pointConversion != null) {
                return new Conversion(fromType, toType, pointConversion);
            }
        }

        return null;
    }

    public Conversion findIntervalPromotion(DataType fromType, IntervalType toType, OperatorMap operatorMap) {
        if (fromType.isSubTypeOf(toType.getPointType())) {
            return new Conversion(fromType, toType, null);
        } else {
            Conversion pointConversion = findConversion(fromType, toType.getPointType(), true, false, operatorMap);
            if (pointConversion != null) {
                return new Conversion(fromType, toType, pointConversion);
            }
        }

        return null;
    }

    public boolean ensureGenericConversionInstantiated(
            DataType fromType, DataType toType, boolean isImplicit, OperatorMap operatorMap) {
        boolean operatorsInstantiated = false;
        for (Conversion c : getGenericConversions()) {
            if (c.getOperator() != null) {
                // instantiate the generic...
                InstantiationResult instantiationResult = ((GenericOperator) c.getOperator())
                        .instantiate(new Signature(fromType), operatorMap, this, false);
                Operator operator = instantiationResult.getOperator();
                if (operator != null && !operatorMap.containsOperator(operator)) {
                    operatorMap.addOperator(operator);
                    Conversion conversion = new Conversion(operator, true);
                    this.add(conversion);
                    operatorsInstantiated = true;
                }
            }
        }

        return operatorsInstantiated;
    }

    private Conversion internalFindConversion(DataType fromType, DataType toType, boolean isImplicit) {
        Conversion result = null;
        int score = Integer.MAX_VALUE;
        for (Conversion conversion : getAllConversions(fromType)) {
            if ((!isImplicit || conversion.isImplicit())) {
                if (conversion.getToType().isSuperTypeOf(toType)
                        || conversion.getToType().isGeneric()) {
                    // Lower score is better. If the conversion matches the target type exactly, the score is 0.
                    // If the conversion is generic, the score is 1 (because that will be instantiated to an exact
                    // match)
                    // If the conversion is a super type, it should only be used if an exact match cannot be found.
                    // If the score is equal to an existing, it indicates a duplicate conversion
                    int newScore = (conversion.getFromType().equals(fromType)
                                    ? 0
                                    : (conversion.getFromType().isGeneric() ? 1 : 2))
                            + (conversion.getToType().equals(toType)
                                    ? 0
                                    : (conversion.getToType().isGeneric() ? 1 : 2));
                    if (newScore < score) {
                        result = conversion;
                        score = newScore;
                    } else if (newScore == score) {
                        // ERROR
                        throw new IllegalArgumentException(String.format(
                                "Ambiguous implicit conversion from %s to %s or %s.",
                                fromType.toString(),
                                result.getToType().toString(),
                                conversion.getToType().toString()));
                    }
                }
            }
        }

        return result;
    }

    public Conversion findConversion(
            DataType fromType,
            DataType toType,
            boolean isImplicit,
            boolean allowPromotionAndDemotion,
            OperatorMap operatorMap) {
        Conversion result = findCompatibleConversion(fromType, toType);
        if (result == null) {
            result = internalFindConversion(fromType, toType, isImplicit);
        }

        if (result == null) {
            if (ensureGenericConversionInstantiated(fromType, toType, isImplicit, operatorMap)) {
                result = internalFindConversion(fromType, toType, isImplicit);
            }
        }

        if (result == null) {
            // NOTE: FHIRPath Implicit conversion from list to singleton
            // If the from type is a list and the target type is a singleton (potentially with a compatible conversion),
            // Convert by invoking a singleton
            if (fromType instanceof ListType
                    && !(toType instanceof ListType)
                    && (allowPromotionAndDemotion || listDemotion)) {
                result = findListDemotion((ListType) fromType, toType, operatorMap);
            }

            if (!(fromType instanceof ListType)
                    && toType instanceof ListType
                    && (allowPromotionAndDemotion || listPromotion)) {
                result = findListPromotion(fromType, (ListType) toType, operatorMap);
            }

            if (fromType instanceof IntervalType
                    && !(toType instanceof IntervalType)
                    && (allowPromotionAndDemotion || intervalDemotion)) {
                result = findIntervalDemotion((IntervalType) fromType, toType, operatorMap);
            }

            if (!(fromType instanceof IntervalType)
                    && toType instanceof IntervalType
                    && (allowPromotionAndDemotion || intervalPromotion)) {
                result = findIntervalPromotion(fromType, (IntervalType) toType, operatorMap);
            }

            // If the from type is a choice, attempt to find a conversion from one of the choice types
            if (fromType instanceof ChoiceType) {
                result = findChoiceConversion((ChoiceType) fromType, toType, allowPromotionAndDemotion, operatorMap);
            }

            // If the target type is a choice, attempt to find a conversion to one of the choice types
            if (!(fromType instanceof ChoiceType) && toType instanceof ChoiceType) {
                result = findTargetChoiceConversion(
                        fromType, (ChoiceType) toType, allowPromotionAndDemotion, operatorMap);
            }

            // If both types are lists, attempt to find a conversion between the element types
            if (fromType instanceof ListType && toType instanceof ListType) {
                result = findListConversion((ListType) fromType, (ListType) toType, operatorMap);
            }

            // If both types are intervals, attempt to find a conversion between the point types
            if (fromType instanceof IntervalType && toType instanceof IntervalType) {
                result = findIntervalConversion((IntervalType) fromType, (IntervalType) toType, operatorMap);
            }
        }

        return result;
    }
}
