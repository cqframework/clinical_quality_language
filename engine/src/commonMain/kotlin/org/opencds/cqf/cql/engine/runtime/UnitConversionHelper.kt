package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.cql2elm.ucum.UcumService
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.ONE
import org.opencds.cqf.cql.engine.execution.State

@Suppress("ReturnCount")
fun <R> computeWithConvertedUnits(
    left: Quantity,
    right: Quantity,
    computation: (kotlin.String, BigDecimal, BigDecimal) -> R,
    state: State?,
): R? {
    val leftUnit = left.unit!!
    val rightUnit = right.unit!!
    // If the units are equal, perform the computation without any conversion.
    if (leftUnit == rightUnit) {
        return computation(leftUnit, left.value!!, right.value!!)
    } else if (state == null) {
        return null
    } else {
        // If the units are not equal, try to convert between the different units. Try the
        // conversion in both directions and select the one for which the result of the
        // computation will be expressed in the more granular unit.
        val leftValue = left.value!!
        val rightValue = right.value!!
        val ucumService = state.environment.libraryManager?.ucumService!!
        val rightConverted = convertIfLessGranular(ucumService, rightValue, rightUnit, leftUnit)
        if (rightConverted != null) {
            return computation(leftUnit, leftValue, rightConverted)
        } else {
            val leftConverted = convertIfLessGranular(ucumService, leftValue, leftUnit, rightUnit)
            if (leftConverted != null) {
                return computation(rightUnit, leftConverted, rightValue)
            }
        }
    }
    // If the units were neither equal nor convertible, don't perform the computation and return
    // null.
    return null
}

/**
 * Converts the two quantities to the more granular (smaller) of their units and applies
 * [computation] to the converted values. This is the conversion Truncated Divide and Modulo use,
 * "after converting to common units in the same way as for division" (FHIR-59659), where division
 * converts to the most granular unit (FHIR-58137), as Add and Subtract do ("The unit of the result
 * will be the most granular unit of either input"). So `1 'm' mod 30 'cm'` is computed as `100 'cm'
 * mod 30 'cm'`.
 *
 * CQL calendar duration keywords (`days`, `weeks`, …) are converted through their UCUM equivalents,
 * and the result keeps the keyword. Years and months only convert to each other: "unit conversion
 * between variable length calendar durations (i.e. years and months) and definite-time durations
 * (i.e. days or below) results in null" (Equal).
 *
 * Returns null if the units can't be converted to each other, or if they differ and [state] is
 * null.
 */
@Suppress("ReturnCount")
fun <R> computeInMoreGranularUnit(
    left: Quantity,
    right: Quantity,
    computation: (kotlin.String, BigDecimal, BigDecimal) -> R,
    state: State?,
): R? {
    val leftUnit = left.unit!!
    val rightUnit = right.unit!!
    if (leftUnit == rightUnit) {
        return computation(leftUnit, left.value!!, right.value!!)
    }
    if (state == null || isVariableCalendarUnit(leftUnit) != isVariableCalendarUnit(rightUnit)) {
        return null
    }
    val ucumService = state.environment.libraryManager?.ucumService!!
    val leftUcumUnit = Quantity.toUcumUnit(leftUnit)
    val rightUcumUnit = Quantity.toUcumUnit(rightUnit)
    return try {
        // One of the left unit, in the right unit. At least 1 means the right unit is the same
        // size or smaller, so it is the more granular of the two.
        if (ucumService.convert(ONE, leftUcumUnit, rightUcumUnit) >= ONE) {
            computation(
                rightUnit,
                ucumService.convert(left.value!!, leftUcumUnit, rightUcumUnit),
                right.value!!,
            )
        } else {
            computation(
                leftUnit,
                left.value!!,
                ucumService.convert(right.value!!, rightUcumUnit, leftUcumUnit),
            )
        }
    } catch (ignored: Exception) {
        // The units aren't commensurable.
        null
    }
}

/** The CQL calendar duration keywords for years and months, whose length varies. */
private fun isVariableCalendarUnit(unit: kotlin.String): kotlin.Boolean {
    return when (unit) {
        "year",
        "years",
        "month",
        "months" -> true
        else -> false
    }
}

fun compareQuantities(leftQuantity: Quantity, rightQuantity: Quantity, state: State?): Int? {
    return if (leftQuantity.value == null || rightQuantity.value == null) {
        null
    } else {
        leftQuantity.nullableCompareTo(rightQuantity)
            ?: computeWithConvertedUnits(
                leftQuantity,
                rightQuantity,
                { _: kotlin.String, leftValue: BigDecimal, rightValue: BigDecimal ->
                    leftValue.compareTo(rightValue)
                },
                state,
            )
    }
}

private fun convertIfLessGranular(
    ucumService: UcumService,
    value: BigDecimal,
    fromUnit: kotlin.String,
    toUnit: kotlin.String,
): BigDecimal? {
    try {
        val convertedDecimal = ucumService.convert(value, fromUnit, toUnit)
        // If the units are equal but spelled differently (for example 'g/m' vs 'g.m-1'), the
        // numeric value may be the same as before, so accept convertedDecimal and value being
        // equal as "less granular".
        if (convertedDecimal <= value) {
            return convertedDecimal
        }
    } catch (ignored: Exception) {}
    return null
}
