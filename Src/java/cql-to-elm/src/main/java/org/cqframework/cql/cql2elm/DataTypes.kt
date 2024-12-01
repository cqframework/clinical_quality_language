package org.cqframework.cql.cql2elm

import org.hl7.cql.model.DataType

object DataTypes {
    @JvmStatic
    fun verifyType(actualType: DataType?, expectedType: DataType?) {
        require(subTypeOf(actualType, expectedType)) {
            // ERROR:
            @Suppress("ImplicitDefaultLocale")
            String.format(
                "Expected an expression of type '%s', but found an expression of type '%s'.",
                if (expectedType != null) expectedType.toLabel() else "<unknown>",
                if (actualType != null) actualType.toLabel() else "<unknown>"
            )
        }
    }

    @JvmStatic
    fun verifyCast(targetType: DataType?, sourceType: DataType?) {
        // Casting can be used for compatible types as well as subtypes and supertypes
        require(
            subTypeOf(targetType, sourceType) ||
                superTypeOf(targetType, sourceType) ||
                compatibleWith(sourceType, targetType)
        ) {
            // ERROR:
            @Suppress("ImplicitDefaultLocale")
            String.format(
                "Expression of type '%s' cannot be cast as a value of type '%s'.",
                if (sourceType != null) sourceType.toLabel() else "<unknown>",
                if (targetType != null) targetType.toLabel() else "<unknown>"
            )
        }
    }

    @JvmStatic
    fun equal(a: DataType?, b: DataType?): Boolean {
        return a != null && b != null && a == b
    }

    fun compatibleWith(a: DataType?, b: DataType?): Boolean {
        return a != null && b != null && a.isCompatibleWith(b)
    }

    @JvmStatic
    fun subTypeOf(a: DataType?, b: DataType?): Boolean {
        return a != null && b != null && a.isSubTypeOf(b)
    }

    fun superTypeOf(a: DataType?, b: DataType?): Boolean {
        return a != null && b != null && a.isSuperTypeOf(b)
    }
}
