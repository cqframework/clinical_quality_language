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
}
