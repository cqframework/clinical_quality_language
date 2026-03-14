package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.DateTimeComponent
import org.hl7.cql.ast.DateTimeComponentExpression
import org.hl7.cql.ast.DifferenceBetweenExpression
import org.hl7.cql.ast.DifferenceOfExpression
import org.hl7.cql.ast.DurationBetweenExpression
import org.hl7.cql.ast.DurationOfExpression
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TimeBoundaryKind
import org.hl7.elm.r1.DateFrom
import org.hl7.elm.r1.DateTimeComponentFrom
import org.hl7.elm.r1.DateTimePrecision
import org.hl7.elm.r1.DifferenceBetween
import org.hl7.elm.r1.DurationBetween
import org.hl7.elm.r1.End
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Start
import org.hl7.elm.r1.TimeFrom
import org.hl7.elm.r1.TimezoneOffsetFrom

/** Emit a [DateTimeComponentExpression] to the appropriate ELM node. */
@Suppress("CyclomaticComplexMethod")
internal fun EmissionContext.emitDateTimeComponent(
    expression: DateTimeComponentExpression
): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    return when (expression.component) {
        DateTimeComponent.DATE -> DateFrom().apply { operand = operandElm }
        DateTimeComponent.TIME -> TimeFrom().apply { operand = operandElm }
        DateTimeComponent.TIMEZONE_OFFSET -> TimezoneOffsetFrom().apply { operand = operandElm }
        else ->
            DateTimeComponentFrom().apply {
                operand = operandElm
                precision = componentToPrecision(expression.component)
            }
    }
}

/** Emit a [DurationBetweenExpression] as an ELM DurationBetween node. */
internal fun EmissionContext.emitDurationBetween(
    expression: DurationBetweenExpression
): ElmExpression {
    val lowerElm = emitExpression(expression.lower)
    val upperElm = emitExpression(expression.upper)
    return DurationBetween().apply {
        precision = precisionStringToEnum(expression.precision)
        operand = mutableListOf(lowerElm, upperElm)
    }
}

/** Emit a [DifferenceBetweenExpression] as an ELM DifferenceBetween node. */
internal fun EmissionContext.emitDifferenceBetween(
    expression: DifferenceBetweenExpression
): ElmExpression {
    val lowerElm = emitExpression(expression.lower)
    val upperElm = emitExpression(expression.upper)
    return DifferenceBetween().apply {
        precision = precisionStringToEnum(expression.precision)
        operand = mutableListOf(lowerElm, upperElm)
    }
}

/**
 * Emit a [DurationOfExpression] (`duration in days of X`) as DurationBetween(Start(X), End(X)). The
 * legacy translator wraps the interval operand in Start/End and emits DurationBetween.
 */
internal fun EmissionContext.emitDurationOf(expression: DurationOfExpression): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    val start = org.hl7.elm.r1.Start().apply { operand = operandElm }
    val end = org.hl7.elm.r1.End().apply { operand = operandElm }
    return DurationBetween().apply {
        precision = precisionStringToEnum(expression.precision)
        operand = mutableListOf(start, end)
    }
}

/**
 * Emit a [DifferenceOfExpression] (`difference in days of X`) as DifferenceBetween(Start(X),
 * End(X)).
 */
internal fun EmissionContext.emitDifferenceOf(expression: DifferenceOfExpression): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    val start = org.hl7.elm.r1.Start().apply { operand = operandElm }
    val end = org.hl7.elm.r1.End().apply { operand = operandElm }
    return DifferenceBetween().apply {
        precision = precisionStringToEnum(expression.precision)
        operand = mutableListOf(start, end)
    }
}

/** Emit a [TimeBoundaryExpression] (start of / end of) as ELM Start or End node. */
internal fun EmissionContext.emitTimeBoundary(expression: TimeBoundaryExpression): ElmExpression {
    val operandElm = emitExpression(expression.operand)
    return when (expression.timeBoundaryKind) {
        TimeBoundaryKind.START -> Start().apply { operand = operandElm }
        TimeBoundaryKind.END -> End().apply { operand = operandElm }
    }
}

/** Map a [DateTimeComponent] enum to the ELM [DateTimePrecision] enum. */
private fun componentToPrecision(component: DateTimeComponent): DateTimePrecision {
    return when (component) {
        DateTimeComponent.YEAR -> DateTimePrecision.YEAR
        DateTimeComponent.MONTH -> DateTimePrecision.MONTH
        DateTimeComponent.WEEK -> DateTimePrecision.WEEK
        DateTimeComponent.DAY -> DateTimePrecision.DAY
        DateTimeComponent.HOUR -> DateTimePrecision.HOUR
        DateTimeComponent.MINUTE -> DateTimePrecision.MINUTE
        DateTimeComponent.SECOND -> DateTimePrecision.SECOND
        DateTimeComponent.MILLISECOND -> DateTimePrecision.MILLISECOND
        else ->
            throw ElmEmitter.UnsupportedNodeException(
                "DateTimeComponent '${component.name}' cannot be converted to precision."
            )
    }
}

/** Map a precision string (e.g., "days", "months") to the ELM DateTimePrecision enum. */
@Suppress("CyclomaticComplexMethod")
internal fun precisionStringToEnum(precision: String): DateTimePrecision {
    return when (precision.lowercase()) {
        "a",
        "year",
        "years" -> DateTimePrecision.YEAR
        "mo",
        "month",
        "months" -> DateTimePrecision.MONTH
        "wk",
        "week",
        "weeks" -> DateTimePrecision.WEEK
        "d",
        "day",
        "days" -> DateTimePrecision.DAY
        "h",
        "hour",
        "hours" -> DateTimePrecision.HOUR
        "min",
        "minute",
        "minutes" -> DateTimePrecision.MINUTE
        "s",
        "second",
        "seconds" -> DateTimePrecision.SECOND
        "ms",
        "millisecond",
        "milliseconds" -> DateTimePrecision.MILLISECOND
        else -> throw ElmEmitter.UnsupportedNodeException("Unknown precision: '$precision'")
    }
}
