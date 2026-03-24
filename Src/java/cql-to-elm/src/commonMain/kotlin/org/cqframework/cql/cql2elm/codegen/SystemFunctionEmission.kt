@file:Suppress("MagicNumber")

package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.shared.QName
import org.hl7.elm.r1.Abs
import org.hl7.elm.r1.CalculateAge
import org.hl7.elm.r1.CalculateAgeAt
import org.hl7.elm.r1.Ceiling
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Date
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.DateTimePrecision
import org.hl7.elm.r1.Exp
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Floor
import org.hl7.elm.r1.GeometricMean
import org.hl7.elm.r1.HighBoundary
import org.hl7.elm.r1.Literal as ElmLiteral
import org.hl7.elm.r1.Ln
import org.hl7.elm.r1.Log
import org.hl7.elm.r1.LowBoundary
import org.hl7.elm.r1.Message
import org.hl7.elm.r1.Now
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.PopulationStdDev
import org.hl7.elm.r1.PopulationVariance
import org.hl7.elm.r1.Precision
import org.hl7.elm.r1.Product
import org.hl7.elm.r1.Round
import org.hl7.elm.r1.Slice
import org.hl7.elm.r1.StdDev
import org.hl7.elm.r1.Time
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
        "GeometricMean" -> emitUnaryArg(args) { GeometricMean().apply { source = it } }

        // Arithmetic unary functions
        "Abs" -> emitUnaryArg(args) { Abs().apply { operand = it } }
        "Ceiling" -> emitUnaryArg(args) { Ceiling().apply { operand = it } }
        "Floor" -> emitUnaryArg(args) { Floor().apply { operand = it } }
        "Truncate" -> emitUnaryArg(args) { Truncate().apply { operand = it } }
        "Ln" -> emitUnaryArg(args) { Ln().apply { operand = it } }
        "Exp" -> emitUnaryArg(args) { Exp().apply { operand = it } }
        "Precision" -> emitUnaryArg(args) { Precision().apply { operand = it } }

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

        // Date/time constructor functions
        "DateTime" -> emitDateTimeConstructor(args)
        "Date" -> emitDateConstructor(args)
        "Time" -> emitTimeConstructor(args)

        // Message (5 args)
        "Message" -> emitMessage(args)

        // List functions -> Slice
        "Skip" -> emitSkip(args)
        "Take" -> emitTake(args)
        "Tail" -> emitTail(args)

        // Age calculation functions → CalculateAge / CalculateAgeAt ELM nodes
        "CalculateAgeInYears" -> emitCalculateAge(args, DateTimePrecision.YEAR)
        "CalculateAgeInMonths" -> emitCalculateAge(args, DateTimePrecision.MONTH)
        "CalculateAgeInWeeks" -> emitCalculateAge(args, DateTimePrecision.WEEK)
        "CalculateAgeInDays" -> emitCalculateAge(args, DateTimePrecision.DAY)
        "CalculateAgeInHours" -> emitCalculateAge(args, DateTimePrecision.HOUR)
        "CalculateAgeInMinutes" -> emitCalculateAge(args, DateTimePrecision.MINUTE)
        "CalculateAgeInSeconds" -> emitCalculateAge(args, DateTimePrecision.SECOND)
        "CalculateAgeInYearsAt" -> emitCalculateAgeAt(args, DateTimePrecision.YEAR)
        "CalculateAgeInMonthsAt" -> emitCalculateAgeAt(args, DateTimePrecision.MONTH)
        "CalculateAgeInWeeksAt" -> emitCalculateAgeAt(args, DateTimePrecision.WEEK)
        "CalculateAgeInDaysAt" -> emitCalculateAgeAt(args, DateTimePrecision.DAY)
        "CalculateAgeInHoursAt" -> emitCalculateAgeAt(args, DateTimePrecision.HOUR)
        "CalculateAgeInMinutesAt" -> emitCalculateAgeAt(args, DateTimePrecision.MINUTE)
        "CalculateAgeInSecondsAt" -> emitCalculateAgeAt(args, DateTimePrecision.SECOND)

        else -> null
    }
}

// Null wrapping for DateTime/Date/Time constructor arguments is handled by ConversionInserter.

@Suppress("CyclomaticComplexMethod")
private fun emitDateTimeConstructor(args: List<ElmExpression>): ElmExpression {
    require(args.size in 1..8) { "Expected 1 to 8 arguments for DateTime" }
    return DateTime().apply {
        year = args[0]
        if (args.size > 1) month = args[1]
        if (args.size > 2) day = args[2]
        if (args.size > 3) hour = args[3]
        if (args.size > 4) minute = args[4]
        if (args.size > 5) second = args[5]
        if (args.size > 6) millisecond = args[6]
        if (args.size > 7) timezoneOffset = args[7]
    }
}

private fun emitDateConstructor(args: List<ElmExpression>): ElmExpression {
    require(args.size in 1..3) { "Expected 1 to 3 arguments for Date" }
    return Date().apply {
        year = args[0]
        if (args.size > 1) month = args[1]
        if (args.size > 2) day = args[2]
    }
}

private fun emitTimeConstructor(args: List<ElmExpression>): ElmExpression {
    require(args.size in 1..4) { "Expected 1 to 4 arguments for Time" }
    return Time().apply {
        hour = args[0]
        if (args.size > 1) minute = args[1]
        if (args.size > 2) second = args[2]
        if (args.size > 3) millisecond = args[3]
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

/** Legacy translates `Skip(list, n)` to `Slice(source=list, startIndex=n, endIndex=Null)`. */
private fun emitSkip(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for Skip" }
    return Slice().apply {
        source = args[0]
        startIndex = args[1]
        endIndex = Null()
    }
}

/**
 * Legacy translates `Take(list, n)` to `Slice(source=list, startIndex=0, endIndex=Coalesce(n, 0))`.
 */
private fun emitTake(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for Take" }
    fun intZero() =
        ElmLiteral().withValueType(QName("urn:hl7-org:elm-types:r1", "Integer")).withValue("0")
    return Slice().apply {
        source = args[0]
        startIndex = intZero()
        endIndex = Coalesce().apply { operand = mutableListOf(args[1], intZero()) }
    }
}

/** Legacy translates `Tail(list)` to `Slice(source=list, startIndex=1, endIndex=Null)`. */
private fun emitTail(args: List<ElmExpression>): ElmExpression {
    require(args.size == 1) { "Expected 1 argument for Tail" }
    return Slice().apply {
        source = args[0]
        startIndex =
            ElmLiteral().withValueType(QName("urn:hl7-org:elm-types:r1", "Integer")).withValue("1")
        endIndex = Null()
    }
}

/**
 * Emit `CalculateAgeInYears(birthDate)` etc. as `CalculateAge(operand, precision)`. ToDate wrapping
 * for Year/Month precision is handled by the ConversionPlanner (OperatorConversion on the argument
 * slot), applied by EmissionContext.applyConversions before we get here.
 */
private fun emitCalculateAge(
    args: List<ElmExpression>,
    precision: DateTimePrecision,
): ElmExpression {
    require(args.size == 1) { "Expected 1 argument for CalculateAge" }
    return CalculateAge().apply {
        operand = args[0]
        this.precision = precision
    }
}

/**
 * Emit `CalculateAgeInYearsAt(birthDate, asOf)` etc. as `CalculateAgeAt([operands], precision)`.
 */
private fun emitCalculateAgeAt(
    args: List<ElmExpression>,
    precision: DateTimePrecision,
): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for CalculateAgeAt" }
    return CalculateAgeAt().apply {
        operand = mutableListOf(args[0], args[1])
        this.precision = precision
    }
}
