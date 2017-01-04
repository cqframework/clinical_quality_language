package org.cqframework.cql.cql2elm;

import org.hl7.cql.model.DataType;

public class DataTypes {
    public static void verifyType(DataType actualType, DataType expectedType) {
        if (!subTypeOf(actualType, expectedType)) {
            throw new IllegalArgumentException(String.format(
                    "Expected an expression of type '%s', but found an expression of type '%s'.",
                    expectedType != null ? expectedType.toLabel() : "<unknown>",
                    actualType != null ? actualType.toLabel() : "<unknown>"
            ));
        }
    }

    public static void verifyCast(DataType targetType, DataType sourceType) {
        // Casting can be used for compatible types as well as subtypes
        if (!(subTypeOf(targetType, sourceType) || compatibleWith(sourceType, targetType))) {
            throw new IllegalArgumentException(String.format("Expression of type '%s' cannot be cast as a value of type '%s'.",
                    sourceType != null ? sourceType.toLabel() : "<unknown>",
                    targetType != null ? targetType.toLabel() : "<unknown>"
            ));
        }
    }

    public static boolean equal(DataType a, DataType b) {
        return a != null && b != null && a.equals(b);
    }

    public static boolean compatibleWith(DataType a, DataType b) {
        return a != null && b != null && a.isCompatibleWith(b);
    }

    public static boolean subTypeOf(DataType a, DataType b) {
        return a != null && b != null && a.isSubTypeOf(b);
    }

    public static boolean superTypeOf(DataType a, DataType b) {
        return a != null && b != null && a.isSuperTypeOf(b);
    }
}
