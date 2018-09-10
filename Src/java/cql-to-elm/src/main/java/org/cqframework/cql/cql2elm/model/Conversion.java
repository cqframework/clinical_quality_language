package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.*;

import java.util.ArrayList;
import java.util.List;

public class Conversion {
    public Conversion(Operator operator, boolean isImplicit) {
        setIsImplicit(isImplicit);
        setOperator(operator);
    }

    public Conversion(DataType fromType, DataType toType) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.isCastFlag = true;
    }

    public Conversion(ChoiceType fromType, DataType toType, Conversion choiceConversion) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.conversionField = choiceConversion;
        this.isCastFlag = true;
    }

    public Conversion(ListType fromType, ListType toType, Conversion elementConversion) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        if (elementConversion == null) {
            throw new IllegalArgumentException("elementConversion is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.conversionField = elementConversion;
        this.isListConversionFlag = true;
    }

    public Conversion(ListType fromType, DataType toType, Conversion elementConversion) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.conversionField = elementConversion;
        this.isListDemotionFlag = true;
    }

    public Conversion(DataType fromType, ListType toType, Conversion elementConversion) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.conversionField = elementConversion;
        this.isListPromotionFlag = true;
    }

    public Conversion(IntervalType fromType, DataType toType, Conversion elementConversion) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }


        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.conversionField = elementConversion;
        this.isIntervalDemotionFlag = true;
    }

    public Conversion(DataType fromType, IntervalType toType, Conversion elementConversion) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.conversionField = elementConversion;
        this.isIntervalPromotionFlag = true;
    }

    public Conversion(IntervalType fromType, IntervalType toType, Conversion pointConversion) {
        if (fromType == null) {
            throw new IllegalArgumentException("fromType is null");
        }

        if (toType == null) {
            throw new IllegalArgumentException("toType is null");
        }

        if (pointConversion == null) {
            throw new IllegalArgumentException("pointConversion is null");
        }

        setIsImplicit(true);
        this.fromType = fromType;
        this.toType = toType;
        this.conversionField = pointConversion;
        this.isIntervalConversionFlag = true;
    }

    private boolean implicit;
    public boolean isImplicit() {
        return implicit;
    }
    public void setIsImplicit(boolean implicit) {
        this.implicit = implicit;
    }

    private Operator operator;
    public Operator getOperator() {
        return operator;
    }
    public void setOperator(Operator operator) {
        if (operator == null) {
            throw new IllegalArgumentException("operator is null");
        }

        // NOTE: FHIRPath Support, need to allow generic conversion operators
//        if (operator instanceof GenericOperator) {
//            throw new IllegalArgumentException("Generic conversion operators are not supported.");
//        }

        fromType = null;
        for (DataType dataType : operator.getSignature().getOperandTypes()) {
            if (fromType != null) {
                throw new IllegalArgumentException("Conversion operator must be unary.");
            }

            fromType = dataType;
        }

        if (fromType == null) {
            throw new IllegalArgumentException("Conversion operator must be unary.");
        }

        toType = operator.getResultType();

        this.operator = operator;
    }

    private Conversion conversionField;
    public Conversion getConversion() {
        return conversionField;
    }

    private List<Conversion> alternativeConversions;
    public List<Conversion> getAlternativeConversions() {
        if (alternativeConversions == null) {
            alternativeConversions = new ArrayList<Conversion>();
        }

        return alternativeConversions;
    }

    public boolean hasAlternativeConversions() {
        return alternativeConversions != null;
    }

    public void addAlternativeConversion(Conversion alternativeConversion) {
        if (!(fromType instanceof ChoiceType)) {
            throw new IllegalArgumentException("Alternative conversions can only be used with choice types");
        }

        // TODO: Should also guard against adding an alternative that is not one of the component types of the fromType
        // This should never happen though with current usage

        getAlternativeConversions().add(alternativeConversion);
    }

    public int getScore() {
        int nestedScore = conversionField != null ? conversionField.getScore() : 0;
        if (isCast()) {
            return ConversionMap.ConversionScore.Cast.score() + nestedScore;
        }
        else if (isIntervalDemotion()) {
            return ConversionMap.ConversionScore.IntervalDemotion.score() + nestedScore;
        }
        else if (isListDemotion()) {
            return ConversionMap.ConversionScore.ListDemotion.score() + nestedScore;
        }
        else if (isIntervalPromotion()) {
            return ConversionMap.ConversionScore.IntervalPromotion.score() + nestedScore;
        }
        else if (isListPromotion()) {
            return ConversionMap.ConversionScore.ListPromotion.score() + nestedScore;
        }
        else if (isListConversion()) {
            if (((ListType)getToType()).getElementType() instanceof SimpleType) {
                return ConversionMap.ConversionScore.SimpleConversion.score() + nestedScore;
            }
            else {
                return ConversionMap.ConversionScore.ComplexConversion.score() + nestedScore;
            }
        }
        else if (isIntervalConversion()) {
            if (((IntervalType)getToType()).getPointType() instanceof SimpleType) {
                return ConversionMap.ConversionScore.SimpleConversion.score() + nestedScore;
            }
            else {
                return ConversionMap.ConversionScore.ComplexConversion.score() + nestedScore;
            }
        }
        else if (getToType() instanceof ClassType) {
            return ConversionMap.ConversionScore.ComplexConversion.score() + nestedScore;
        }
        else {
            return ConversionMap.ConversionScore.SimpleConversion.score() + nestedScore;
        }
    }

    public boolean isGeneric() {
        return this.operator instanceof GenericOperator;
    }

    private boolean isCastFlag;
    public boolean isCast() {
        return isCastFlag;
    }

    private boolean isListConversionFlag;
    public boolean isListConversion() {
        return isListConversionFlag;
    }

    private boolean isListPromotionFlag;
    public boolean isListPromotion() {
        return isListPromotionFlag;
    }

    private boolean isListDemotionFlag;
    public boolean isListDemotion() {
        return isListDemotionFlag;
    }

    private boolean isIntervalConversionFlag;
    public boolean isIntervalConversion() {
        return isIntervalConversionFlag;
    }

    private boolean isIntervalPromotionFlag;
    public boolean isIntervalPromotion() {
        return isIntervalPromotionFlag;
    }

    private boolean isIntervalDemotionFlag;
    public boolean isIntervalDemotion() {
        return isIntervalDemotionFlag;
    }

    private DataType fromType;
    public DataType getFromType() {
        return fromType;
    }

    private DataType toType;
    public DataType getToType() {
        return toType;
    }
}
