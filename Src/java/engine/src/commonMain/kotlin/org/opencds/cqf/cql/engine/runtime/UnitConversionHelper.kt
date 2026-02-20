package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.cql2elm.ucum.UcumService
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.execution.State

@Suppress("ReturnCount")
fun <R> computeWithConvertedUnits(
    left: Quantity,
    right: Quantity,
    computation: (String, BigDecimal, BigDecimal) -> R,
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

fun compareQuantities(leftQuantity: Quantity, rightQuantity: Quantity, state: State?): Int? {
    return if (leftQuantity.value == null || rightQuantity.value == null) {
        null
    } else {
        leftQuantity.nullableCompareTo(rightQuantity)
            ?: computeWithConvertedUnits(
                leftQuantity,
                rightQuantity,
                { _: String, leftValue: BigDecimal, rightValue: BigDecimal ->
                    leftValue.compareTo(rightValue)
                },
                state,
            )
    }
}

private fun convertIfLessGranular(
    ucumService: UcumService,
    value: BigDecimal,
    fromUnit: String,
    toUnit: String,
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
