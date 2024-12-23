package org.cqframework.cql.cql2elm

import java.util.*
import org.hl7.cql.model.DataType

private const val UNKNOWN = "<unknown>"

object DataTypes {
    fun verifyType(actualType: DataType?, expectedType: DataType?) {
        require(subTypeOf(actualType, expectedType)) {
            // ERROR:
            String.format(
                Locale.US,
                "Expected an expression of type '%s', but found an expression of type '%s'.",
                expectedType?.toLabel() ?: UNKNOWN,
                actualType?.toLabel() ?: UNKNOWN
            )
        }
    }

    fun verifyCast(targetType: DataType?, sourceType: DataType?) {
        // Casting can be used for compatible types as well as subtypes and supertypes
        require(
            subTypeOf(targetType, sourceType) ||
                superTypeOf(targetType, sourceType) ||
                compatibleWith(sourceType, targetType)
        ) {
            // ERROR:
            String.format(
                Locale.US,
                "Expression of type '%s' cannot be cast as a value of type '%s'.",
                sourceType?.toLabel() ?: UNKNOWN,
                targetType?.toLabel() ?: UNKNOWN
            )
        }
    }

    fun equal(a: DataType?, b: DataType?): Boolean {
        return a != null && b != null && a == b
    }

    private fun compatibleWith(a: DataType?, b: DataType?): Boolean {
        return a != null && b != null && a.isCompatibleWith(b)
    }

    fun subTypeOf(a: DataType?, b: DataType?): Boolean {
        return a != null && b != null && a.isSubTypeOf(b)
    }

    private fun superTypeOf(a: DataType?, b: DataType?): Boolean {
        return a != null && b != null && a.isSuperTypeOf(b)
    }
}
