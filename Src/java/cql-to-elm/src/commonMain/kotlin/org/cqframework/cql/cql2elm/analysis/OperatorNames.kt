package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.UnaryOperator

/** Maps AST operator enums to their System library operator names. */
object OperatorNames {

    @Suppress("CyclomaticComplexMethod")
    fun binaryOperatorToSystemName(op: BinaryOperator): String? {
        return when (op) {
            BinaryOperator.ADD -> "Add"
            BinaryOperator.SUBTRACT -> "Subtract"
            BinaryOperator.MULTIPLY -> "Multiply"
            BinaryOperator.DIVIDE -> "Divide"
            BinaryOperator.TRUNCATED_DIVIDE -> "TruncatedDivide"
            BinaryOperator.MODULO -> "Modulo"
            BinaryOperator.POWER -> "Power"
            BinaryOperator.CONCAT -> "Concatenate"
            BinaryOperator.EQUALS -> "Equal"
            BinaryOperator.NOT_EQUALS -> "Equal" // NotEqual is Not(Equal(...))
            BinaryOperator.EQUIVALENT -> "Equivalent"
            BinaryOperator.NOT_EQUIVALENT -> "Equivalent" // Not(Equivalent(...))
            BinaryOperator.LT -> "Less"
            BinaryOperator.LTE -> "LessOrEqual"
            BinaryOperator.GT -> "Greater"
            BinaryOperator.GTE -> "GreaterOrEqual"
            BinaryOperator.AND -> "And"
            BinaryOperator.OR -> "Or"
            BinaryOperator.XOR -> "Xor"
            BinaryOperator.IMPLIES -> "Implies"
            BinaryOperator.UNION -> "Union"
            BinaryOperator.INTERSECT -> "Intersect"
            BinaryOperator.EXCEPT -> "Except"
        }
    }

    fun unaryOperatorToSystemName(op: UnaryOperator): String? {
        return when (op) {
            UnaryOperator.NEGATE -> "Negate"
            UnaryOperator.NOT -> "Not"
            UnaryOperator.SUCCESSOR -> "Successor"
            UnaryOperator.PREDECESSOR -> "Predecessor"
            UnaryOperator.POSITIVE -> "Positive"
        }
    }

    /**
     * FHIRPath fluent function names that map directly to CQL system operators. These are
     * spec-defined by FHIRPath, not model-specific. The fluent call `x.convertsToString()` resolves
     * as the system operator `ConvertsToString(x)`.
     */
    fun fluentFunctionToSystemName(name: String): String? = FHIRPATH_FLUENT_ALIASES[name]

    private val FHIRPATH_FLUENT_ALIASES =
        mapOf(
            "convertsToBoolean" to "ConvertsToBoolean",
            "convertsToDate" to "ConvertsToDate",
            "convertsToDateTime" to "ConvertsToDateTime",
            "convertsToDecimal" to "ConvertsToDecimal",
            "convertsToInteger" to "ConvertsToInteger",
            "convertsToLong" to "ConvertsToLong",
            "convertsToQuantity" to "ConvertsToQuantity",
            "convertsToString" to "ConvertsToString",
            "convertsToTime" to "ConvertsToTime",
            "toChars" to "ToChars",
            "upper" to "Upper",
            "lower" to "Lower",
            "trim" to "Trim",
            "length" to "Length",
            "count" to "Count",
            "sum" to "Sum",
            "min" to "Min",
            "max" to "Max",
            "avg" to "Avg",
            "abs" to "Abs",
            "ceiling" to "Ceiling",
            "floor" to "Floor",
            "truncate" to "Truncate",
            "ln" to "Ln",
            "log" to "Log",
            "exp" to "Exp",
            "sqrt" to "SquareRoot",
        )
}
