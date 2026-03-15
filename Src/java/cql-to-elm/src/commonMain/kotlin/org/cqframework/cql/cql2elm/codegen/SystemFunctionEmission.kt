@file:Suppress("MagicNumber")

package org.cqframework.cql.cql2elm.codegen

import org.hl7.elm.r1.Abs
import org.hl7.elm.r1.Ceiling
import org.hl7.elm.r1.Exp
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Floor
import org.hl7.elm.r1.HighBoundary
import org.hl7.elm.r1.Ln
import org.hl7.elm.r1.Log
import org.hl7.elm.r1.LowBoundary
import org.hl7.elm.r1.Message
import org.hl7.elm.r1.Now
import org.hl7.elm.r1.PopulationStdDev
import org.hl7.elm.r1.PopulationVariance
import org.hl7.elm.r1.Product
import org.hl7.elm.r1.Round
import org.hl7.elm.r1.StdDev
import org.hl7.elm.r1.TimeOfDay
import org.hl7.elm.r1.Today
import org.hl7.elm.r1.Truncate
import org.hl7.elm.r1.Variance

/**
 * Dispatch system functions that are not string or list operations. Returns the ELM expression, or
 * null if the function name is not a recognized system function handled by this file.
 */
@Suppress("CyclomaticComplexMethod")
internal fun emitSystemFunction(functionName: String, args: List<ElmExpression>): ElmExpression? {
    return when (functionName) {
        // Aggregate functions (source-based) — additional
        "PopulationStdDev" -> requireUnary(args) { PopulationStdDev().apply { source = it } }
        "PopulationVariance" -> requireUnary(args) { PopulationVariance().apply { source = it } }
        "StdDev" -> requireUnary(args) { StdDev().apply { source = it } }
        "Variance" -> requireUnary(args) { Variance().apply { source = it } }
        "Product" -> requireUnary(args) { Product().apply { source = it } }

        // Arithmetic unary functions
        "Abs" -> requireUnary(args) { Abs().apply { operand = it } }
        "Ceiling" -> requireUnary(args) { Ceiling().apply { operand = it } }
        "Floor" -> requireUnary(args) { Floor().apply { operand = it } }
        "Truncate" -> requireUnary(args) { Truncate().apply { operand = it } }
        "Ln" -> requireUnary(args) { Ln().apply { operand = it } }
        "Exp" -> requireUnary(args) { Exp().apply { operand = it } }

        // Arithmetic binary functions
        "Log" -> requireBinary(args) { a, b -> Log().apply { operand = mutableListOf(a, b) } }
        "HighBoundary" ->
            requireBinary(args) { a, b -> HighBoundary().apply { operand = mutableListOf(a, b) } }
        "LowBoundary" ->
            requireBinary(args) { a, b -> LowBoundary().apply { operand = mutableListOf(a, b) } }

        // Round (1 or 2 args)
        "Round" -> emitRound(args)

        // Date/time zero-arg functions
        "Now" -> Now()
        "Today" -> Today()
        "TimeOfDay" -> TimeOfDay()

        // Message (5 args)
        "Message" -> emitMessage(args)

        else -> null
    }
}

private fun requireUnary(
    args: List<ElmExpression>,
    factory: (ElmExpression) -> ElmExpression,
): ElmExpression {
    require(args.size == 1) { "Expected 1 argument" }
    return factory(args[0])
}

private fun requireBinary(
    args: List<ElmExpression>,
    factory: (ElmExpression, ElmExpression) -> ElmExpression,
): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments" }
    return factory(args[0], args[1])
}

private fun emitRound(args: List<ElmExpression>): ElmExpression {
    require(args.size in 1..2) { "Expected 1 or 2 arguments for Round" }
    return Round().apply {
        operand = args[0]
        if (args.size > 1) {
            precision = args[1]
        }
    }
}

private fun emitMessage(args: List<ElmExpression>): ElmExpression {
    require(args.size == 5) { "Expected 5 arguments for Message" }
    return Message().apply {
        source = args[0]
        condition = args[1]
        code = args[2]
        severity = args[3]
        message = args[4]
    }
}
