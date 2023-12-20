package org.cqframework.cql.cql2elm.model;

import java.util.ArrayList;
import java.util.List;
import org.hl7.cql.model.ChoiceType;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.InstantiationContext;

public class Signature {
    public Signature(DataType... operandTypes) {
        if (operandTypes == null) {
            throw new IllegalArgumentException("operandTypes is null");
        }

        for (DataType operandType : operandTypes) {
            if (operandType == null) {
                throw new IllegalArgumentException("operandTypes in signatures cannot be null");
            }

            this.operandTypes.add(operandType);
        }
    }

    private List<DataType> operandTypes = new ArrayList<>();

    public Iterable<DataType> getOperandTypes() {
        return this.operandTypes;
    }

    public int getSize() {
        return operandTypes.size();
    }

    public boolean isSuperTypeOf(Signature other) {
        if (operandTypes.size() == other.operandTypes.size()) {
            for (int i = 0; i < operandTypes.size(); i++) {
                if (!operandTypes.get(i).isSuperTypeOf(other.operandTypes.get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    private boolean getHasChoices() {
        for (DataType operandType : operandTypes) {
            if (operandType instanceof ChoiceType) {
                return true;
            }
        }

        return false;
    }

    private boolean hasChoices;
    private boolean calculatedHasChoices;

    public boolean containsChoices() {
        if (!calculatedHasChoices) {
            hasChoices = getHasChoices();
            calculatedHasChoices = true;
        }
        return hasChoices;
    }

    public boolean isSubTypeOf(Signature other) {
        if (operandTypes.size() == other.operandTypes.size()) {
            for (int i = 0; i < operandTypes.size(); i++) {
                if (!operandTypes.get(i).isSubTypeOf(other.operandTypes.get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public boolean isInstantiable(Signature callSignature, InstantiationContext context) {
        if (operandTypes.size() == callSignature.operandTypes.size()) {
            for (int i = 0; i < operandTypes.size(); i++) {
                if (!operandTypes.get(i).isInstantiable(callSignature.operandTypes.get(i), context)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public Signature instantiate(InstantiationContext context) {
        DataType[] result = new DataType[operandTypes.size()];
        for (int i = 0; i < operandTypes.size(); i++) {
            result[i] = operandTypes.get(i).instantiate(context);
        }

        return new Signature(result);
    }

    public boolean isConvertibleTo(
            Signature other,
            ConversionMap conversionMap,
            OperatorMap operatorMap,
            boolean allowPromotionAndDemotion,
            Conversion[] conversions) {
        if (operandTypes.size() == other.operandTypes.size()) {
            for (int i = 0; i < operandTypes.size(); i++) {
                if (!operandTypes.get(i).isSubTypeOf(other.operandTypes.get(i))) {
                    Conversion conversion = conversionMap.findConversion(
                            operandTypes.get(i),
                            other.operandTypes.get(i),
                            true,
                            allowPromotionAndDemotion,
                            operatorMap);
                    if (conversion != null) {
                        conversions[i] = conversion;
                    } else {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = 53;
        for (DataType operandType : operandTypes) {
            result += (39 * operandType.hashCode());
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Signature) {
            Signature that = (Signature) o;

            if (this.operandTypes.size() == that.operandTypes.size()) {
                for (int i = 0; i < this.operandTypes.size(); i++) {
                    if (!(this.operandTypes.get(i).equals(that.operandTypes.get(i)))) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i < operandTypes.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }

            builder.append(operandTypes.get(i).toString());
        }
        builder.append(")");
        return builder.toString();
    }
}
