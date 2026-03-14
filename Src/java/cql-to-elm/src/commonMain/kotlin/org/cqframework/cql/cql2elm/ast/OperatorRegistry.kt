package org.cqframework.cql.cql2elm.ast

import org.hl7.cql.model.DataType
import org.hl7.cql.model.SimpleType

/**
 * Lightweight operator resolution for the new AST-based pipeline. Registers system operators for
 * arithmetic, comparison, and logical operations, and resolves them given operand types. Handles
 * implicit conversions (e.g., Integer + Decimal promotes Integer to Decimal).
 *
 * This avoids pulling in the full SystemLibraryHelper/TypeBuilder/ModelManager stack while still
 * producing output compatible with the legacy translator.
 */
@Suppress("TooManyFunctions")
class OperatorRegistry {
    /** Result of resolving an operator: the system operator name, result type, and conversions. */
    data class Resolution(
        val operatorName: String,
        val resultType: DataType,
        /** Conversion operator name for each operand, or null if no conversion needed. */
        val conversions: List<String?>,
    )

    // System types by simple name
    private val typesByName = mutableMapOf<String, DataType>()

    // Binary operators: name -> list of (left type, right type, result type)
    private val binaryOps = mutableListOf<BinaryOpDef>()

    // Unary operators: name -> list of (operand type, result type)
    private val unaryOps = mutableListOf<UnaryOpDef>()

    // Implicit conversions: from type -> (to type, conversion operator name)
    private val implicitConversions = mutableListOf<ImplicitConversion>()

    private data class BinaryOpDef(
        val name: String,
        val leftType: DataType,
        val rightType: DataType,
        val resultType: DataType,
    )

    private data class UnaryOpDef(
        val name: String,
        val operandType: DataType,
        val resultType: DataType,
    )

    private data class ImplicitConversion(
        val fromType: DataType,
        val toType: DataType,
        val operatorName: String,
    )

    fun registerType(name: String, type: DataType) {
        typesByName[name] = type
    }

    fun type(name: String): DataType =
        typesByName[name] ?: throw IllegalArgumentException("Unknown type: $name")

    fun registerBinaryOp(
        name: String,
        leftType: DataType,
        rightType: DataType,
        resultType: DataType,
    ) {
        binaryOps.add(BinaryOpDef(name, leftType, rightType, resultType))
    }

    fun registerUnaryOp(name: String, operandType: DataType, resultType: DataType) {
        unaryOps.add(UnaryOpDef(name, operandType, resultType))
    }

    fun registerImplicitConversion(fromType: DataType, toType: DataType, operatorName: String) {
        implicitConversions.add(ImplicitConversion(fromType, toType, operatorName))
    }

    /** Resolve a binary operator given operand types. Returns null if no match found. */
    @Suppress("NestedBlockDepth", "CyclomaticComplexMethod", "ReturnCount")
    fun resolveBinary(name: String, leftType: DataType, rightType: DataType): Resolution? {
        // 1. Try exact match
        for (op in binaryOps) {
            if (op.name == name && op.leftType == leftType && op.rightType == rightType) {
                return Resolution(name, op.resultType, listOf(null, null))
            }
        }

        // 2. Try with implicit conversions
        // Try converting left operand
        for (conv in implicitConversions) {
            if (conv.fromType == leftType) {
                for (op in binaryOps) {
                    if (
                        op.name == name && op.leftType == conv.toType && op.rightType == rightType
                    ) {
                        return Resolution(name, op.resultType, listOf(conv.operatorName, null))
                    }
                }
            }
        }
        // Try converting right operand
        for (conv in implicitConversions) {
            if (conv.fromType == rightType) {
                for (op in binaryOps) {
                    if (op.name == name && op.leftType == leftType && op.rightType == conv.toType) {
                        return Resolution(name, op.resultType, listOf(null, conv.operatorName))
                    }
                }
            }
        }
        // Try converting both operands
        for (convL in implicitConversions) {
            if (convL.fromType == leftType) {
                for (convR in implicitConversions) {
                    if (convR.fromType == rightType) {
                        for (op in binaryOps) {
                            if (
                                op.name == name &&
                                    op.leftType == convL.toType &&
                                    op.rightType == convR.toType
                            ) {
                                return Resolution(
                                    name,
                                    op.resultType,
                                    listOf(convL.operatorName, convR.operatorName),
                                )
                            }
                        }
                    }
                }
            }
        }

        return null
    }

    /** Resolve a unary operator given operand type. Returns null if no match found. */
    @Suppress("NestedBlockDepth", "ReturnCount")
    fun resolveUnary(name: String, operandType: DataType): Resolution? {
        // 1. Try exact match
        for (op in unaryOps) {
            if (op.name == name && op.operandType == operandType) {
                return Resolution(name, op.resultType, listOf(null))
            }
        }

        // 2. Try with implicit conversion
        for (conv in implicitConversions) {
            if (conv.fromType == operandType) {
                for (op in unaryOps) {
                    if (op.name == name && op.operandType == conv.toType) {
                        return Resolution(name, op.resultType, listOf(conv.operatorName))
                    }
                }
            }
        }

        return null
    }

    companion object {
        /**
         * Create an OperatorRegistry pre-loaded with the System library operators needed for
         * arithmetic and comparison operations on primitive types.
         */
        @Suppress("LongMethod")
        fun createSystemRegistry(): OperatorRegistry {
            val reg = OperatorRegistry()

            // Register system types
            val any = SimpleType("Any")
            val boolean = SimpleType("Boolean", any)
            val integer = SimpleType("Integer", any)
            val long = SimpleType("Long", any)
            val decimal = SimpleType("Decimal", any)
            val string = SimpleType("String", any)
            val dateTime = SimpleType("DateTime", any)
            val date = SimpleType("Date", any)
            val time = SimpleType("Time", any)
            val quantity = SimpleType("Quantity", any)

            reg.registerType("Any", any)
            reg.registerType("Boolean", boolean)
            reg.registerType("Integer", integer)
            reg.registerType("Long", long)
            reg.registerType("Decimal", decimal)
            reg.registerType("String", string)
            reg.registerType("DateTime", dateTime)
            reg.registerType("Date", date)
            reg.registerType("Time", time)
            reg.registerType("Quantity", quantity)

            // Implicit conversions (matches SystemLibraryHelper)
            reg.registerImplicitConversion(integer, long, "ToLong")
            reg.registerImplicitConversion(integer, decimal, "ToDecimal")
            reg.registerImplicitConversion(long, decimal, "ToDecimal")

            // Arithmetic operators
            registerArithmeticOps(reg, integer, long, decimal, quantity, string)

            // Comparison operators
            registerComparisonOps(
                reg,
                boolean,
                integer,
                long,
                decimal,
                string,
                dateTime,
                date,
                time,
                quantity,
            )

            // Logical operators
            reg.registerBinaryOp("And", boolean, boolean, boolean)
            reg.registerBinaryOp("Or", boolean, boolean, boolean)
            reg.registerBinaryOp("Xor", boolean, boolean, boolean)
            reg.registerBinaryOp("Implies", boolean, boolean, boolean)
            reg.registerUnaryOp("Not", boolean, boolean)

            return reg
        }

        @Suppress("LongParameterList")
        private fun registerArithmeticOps(
            reg: OperatorRegistry,
            integer: DataType,
            long: DataType,
            decimal: DataType,
            quantity: DataType,
            string: DataType,
        ) {
            // Add
            for (t in listOf(integer, long, decimal, quantity)) {
                reg.registerBinaryOp("Add", t, t, t)
            }
            reg.registerBinaryOp("Add", string, string, string)

            // Subtract
            for (t in listOf(integer, long, decimal, quantity)) {
                reg.registerBinaryOp("Subtract", t, t, t)
            }

            // Multiply
            for (t in listOf(integer, long, decimal, quantity)) {
                reg.registerBinaryOp("Multiply", t, t, t)
            }

            // Divide (only Decimal and Quantity - Integer/Long division → Decimal via conversion)
            reg.registerBinaryOp("Divide", decimal, decimal, decimal)
            reg.registerBinaryOp("Divide", quantity, quantity, quantity)

            // Modulo
            for (t in listOf(integer, long, decimal, quantity)) {
                reg.registerBinaryOp("Modulo", t, t, t)
            }

            // Power
            for (t in listOf(integer, long, decimal)) {
                reg.registerBinaryOp("Power", t, t, t)
            }

            // Concatenate
            reg.registerBinaryOp("Concatenate", string, string, string)

            // Unary arithmetic
            for (t in listOf(integer, long, decimal, quantity)) {
                reg.registerUnaryOp("Negate", t, t)
                reg.registerUnaryOp("Successor", t, t)
                reg.registerUnaryOp("Predecessor", t, t)
            }
        }

        @Suppress("LongParameterList")
        private fun registerComparisonOps(
            reg: OperatorRegistry,
            boolean: DataType,
            integer: DataType,
            long: DataType,
            decimal: DataType,
            string: DataType,
            dateTime: DataType,
            date: DataType,
            time: DataType,
            quantity: DataType,
        ) {
            val allComparableTypes =
                listOf(boolean, integer, long, decimal, string, dateTime, date, time, quantity)

            for (t in allComparableTypes) {
                reg.registerBinaryOp("Equal", t, t, boolean)
                reg.registerBinaryOp("Equivalent", t, t, boolean)
            }

            // Ordering comparisons (not for Boolean)
            val orderableTypes =
                listOf(integer, long, decimal, string, dateTime, date, time, quantity)
            for (t in orderableTypes) {
                reg.registerBinaryOp("Less", t, t, boolean)
                reg.registerBinaryOp("LessOrEqual", t, t, boolean)
                reg.registerBinaryOp("Greater", t, t, boolean)
                reg.registerBinaryOp("GreaterOrEqual", t, t, boolean)
            }
        }
    }
}
