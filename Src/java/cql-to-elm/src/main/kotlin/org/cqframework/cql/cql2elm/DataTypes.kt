package org.cqframework.cql.cql2elm

import java.util.*
import org.hl7.cql.model.DataType

private const val UNKNOWN = "<unknown>"

object DataTypes {
    fun verifyType(actualType: DataType?, expectedType: DataType?) {
        require(subTypeOf(actualType, expectedType)) {
            // ERROR:
            "Expected an expression of type '${expectedType?.toLabel() ?: UNKNOWN}'," +
                "but found an expression of type '${actualType?.toLabel() ?: UNKNOWN}'."
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
            "Expression of type '${sourceType?.toLabel() ?: UNKNOWN}'" +
                " cannot be cast as a value of type '${targetType?.toLabel() ?: UNKNOWN}'."
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
