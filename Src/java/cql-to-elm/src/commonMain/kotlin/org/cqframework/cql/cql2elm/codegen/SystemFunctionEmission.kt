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
        "PopulationStdDev" -> emitUnaryArg(args) { PopulationStdDev().apply { source = it } }
        "PopulationVariance" -> emitUnaryArg(args) { PopulationVariance().apply { source = it } }
        "StdDev" -> emitUnaryArg(args) { StdDev().apply { source = it } }
        "Variance" -> emitUnaryArg(args) { Variance().apply { source = it } }
        "Product" -> emitUnaryArg(args) { Product().apply { source = it } }

        // Arithmetic unary functions
        "Abs" -> emitUnaryArg(args) { Abs().apply { operand = it } }
        "Ceiling" -> emitUnaryArg(args) { Ceiling().apply { operand = it } }
        "Floor" -> emitUnaryArg(args) { Floor().apply { operand = it } }
        "Truncate" -> emitUnaryArg(args) { Truncate().apply { operand = it } }
        "Ln" -> emitUnaryArg(args) { Ln().apply { operand = it } }
        "Exp" -> emitUnaryArg(args) { Exp().apply { operand = it } }

        // Arithmetic binary functions
        "Log" -> emitBinaryArgs(args) { a, b -> Log().apply { operand = mutableListOf(a, b) } }
        "HighBoundary" ->
            emitBinaryArgs(args) { a, b -> HighBoundary().apply { operand = mutableListOf(a, b) } }
        "LowBoundary" ->
            emitBinaryArgs(args) { a, b -> LowBoundary().apply { operand = mutableListOf(a, b) } }

        // Round (1 or 2 args)
        "Round" -> emitRound(args)

        // Date/time zero-arg functions
        "Now" -> emitNullaryArg(args) { Now() }
        "Today" -> emitNullaryArg(args) { Today() }
        "TimeOfDay" -> emitNullaryArg(args) { TimeOfDay() }

        // Message (5 args)
        "Message" -> emitMessage(args)

        else -> null
    }
}

private fun emitRound(args: List<ElmExpression>): ElmExpression {
    require(args.size in 1..2) { "Expected 1 or 2 arguments for Round" }
    return Round().apply {
        operand = args[0]
        if (args.size > 1) precision = args[1]
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
