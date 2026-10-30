package org.opencds.cqf.cql.engine.util

import kotlin.collections.linkedMapOf
import kotlin.text.Regex
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.execution.State

enum class UnitOperation {
    MULTIPLY,
    DIVIDE,
}

@Suppress("TooManyFunctions", "ReturnCount")
object UnitUtils {
    private val UNIT_REGEX = Regex("""^(.*[^-\d])?([-]?\d*)$""")

    private fun checkUnit(unit: String?): String {
        return if (unit.isNullOrBlank()) "1" else unit
    }

    private fun isQuotient(unit: String): Boolean {
        return unit.contains('/')
    }

    private fun isMultiple(unit: String): Boolean {
        return unit.contains('.')
    }

    private fun isComplexUnit(unit: String): Boolean {
        return isMultiple(unit) || isQuotient(unit)
    }

    private fun getBaseUnitAndPower(unit: String): Pair<String, Int> {

        if (isComplexUnit(unit)) {
            return unit to 1
        }

        val fixedUnit = checkUnit(unit)
        val match = UNIT_REGEX.matchEntire(fixedUnit) ?: error("Invalid unit format: $fixedUnit")

        var term = match.groups[1]?.value
        var power = match.groups[2]?.value

        if (term.isNullOrBlank()) {
            term = power
            power = "1"
        } else {
            power = power.takeUnless { it.isNullOrBlank() } ?: "1"
        }

        return (term ?: error("Missing base unit")) to power.toInt()
    }

    private fun convertToBaseUnit(
        fromVal: BigDecimal,
        fromUnit: String,
        toBaseUnit: String,
        state: State,
    ): Pair<BigDecimal, String>? {
        val fromPower = getBaseUnitAndPower(fromUnit).second
        val toUnit =
            if (fromPower == 1) {
                toBaseUnit
            } else {
                "${toBaseUnit}${fromPower}"
            }

        val newVal = convertUnit(fromVal, fromUnit, toUnit, state)

        return if (newVal != null) {
            newVal to toUnit
        } else {
            null
        }
    }

    private fun combineQuotients(left: String, right: String, state: State): String? {
        fun splitUnit(unit: String): Pair<String, String> {
            val parts = unit.split("/", limit = 2)
            val numerator = parts[0].ifBlank { "1" }
            val denominator = parts.getOrNull(1)?.ifBlank { "1" } ?: "1"

            return numerator to denominator
        }

        val (leftNum, leftDen) = splitUnit(left)
        val (rightNum, rightDen) = splitUnit(right)

        val numerator =
            combineUnitsInternal(leftNum, rightNum, UnitOperation.MULTIPLY, state) ?: return null

        val denominator =
            combineUnitsInternal(leftDen, rightDen, UnitOperation.MULTIPLY, state) ?: return null

        return combineUnitsInternal(numerator, denominator, UnitOperation.DIVIDE, state)
    }

    private fun formatFactors(factorPowers: MutableMap<String, Int>, positive: Boolean): String {
        return factorPowers.entries
            .filter { (base, power) -> base != "1" && if (positive) power > 0 else power < 0 }
            .joinToString(".") { (base, power) ->
                val exponent = kotlin.math.abs(power)
                "$base${if (exponent > 1) exponent else ""}"
            }
    }

    private fun addFactors(factorPowers: MutableMap<String, Int>, unit: String, sign: Int) {
        unit.split('.').forEach { factor ->
            val (base, power) = getBaseUnitAndPower(factor)
            factorPowers[base] = (factorPowers[base] ?: 0) + sign * power
        }
    }

    private fun combineFactors(left: String, right: String, isDivision: Boolean): String? {
        val factorPowers = linkedMapOf<String, Int>()

        addFactors(factorPowers, left, 1)
        addFactors(factorPowers, right, if (isDivision) -1 else 1)

        val numerator = formatFactors(factorPowers, true)
        val rawDenominator = formatFactors(factorPowers, false)
        val denominator = if (isMultiple(rawDenominator)) "($rawDenominator)" else rawDenominator

        val result =
            when {
                numerator.isEmpty() && denominator.isEmpty() -> "1"
                denominator.isEmpty() -> numerator
                numerator.isEmpty() -> "/$denominator"
                else -> "$numerator/$denominator"
            }

        return checkUnit(result)
    }

    private fun combineUnitsInternal(
        left: String,
        right: String,
        operation: UnitOperation,
        state: State,
    ): String? {
        val isDivision = operation == UnitOperation.DIVIDE

        if (!isDivision && (isQuotient(left) || isQuotient(right))) {
            return combineQuotients(left, right, state)
        }

        if (!isQuotient(left) && !isQuotient(right)) {
            return combineFactors(left, right, isDivision)
        }

        if (isDivision) {
            if (left == right) return "1"
            if (right == "1") return left

            val denominator = if (isComplexUnit(right)) "($right)" else right

            return if (left == "1") "/$denominator" else "$left/$denominator"
        }

        if (left == "1") return right
        if (right == "1") return left

        return "$left.$right"
    }

    fun convertToMostGranularCommeasurableUnit(
        val1: BigDecimal,
        unit1: String,
        val2: BigDecimal,
        unit2: String,
        state: State,
    ): Pair<Pair<BigDecimal, String>, Pair<BigDecimal, String>> {

        var normalizedUnit1 = checkUnit(unit1)
        var normalizedUnit2 = checkUnit(unit2)

        if (normalizedUnit1 == normalizedUnit2) {
            return Pair(val1, normalizedUnit1) to Pair(val2, normalizedUnit2)
        }

        val baseUnit1 = getBaseUnitAndPower(normalizedUnit1).first
        val baseUnit2 = getBaseUnitAndPower(normalizedUnit2).first

        val conversion2 = convertToBaseUnit(val2, normalizedUnit2, baseUnit1, state)

        if (conversion2 == null) {
            // The units were not convertible; return the quantities as-is
            return Pair(val1, normalizedUnit1) to Pair(val2, normalizedUnit2)
        }

        val newVal2 = conversion2.first
        val newUnit2 = conversion2.second

        // Prefer conversion to smaller units when the numeric value increases
        if (newVal2 >= val2) {
            return Pair(Pair(val1, normalizedUnit1), Pair(newVal2, newUnit2))
        }

        // Otherwise, try converting the first quantity instead
        val conversion1 = convertToBaseUnit(val1, normalizedUnit1, baseUnit2, state)

        if (conversion1 == null) {
            // Fallback: retain the successful second-quantity conversion
            return Pair(val1, normalizedUnit1) to Pair(newVal2, newUnit2)
        }

        val newVal1 = conversion1.first
        val newUnit1 = conversion1.second

        return Pair(newVal1, newUnit1) to Pair(val2, normalizedUnit2)
    }

    fun combineUnits(
        unit1: String,
        unit2: String,
        operation: UnitOperation,
        state: State?,
    ): String? {
        val left = checkUnit(unit1)
        val right = checkUnit(unit2)
        val ucumService = state?.environment?.libraryManager?.ucumService!!

        if (ucumService.validate(left) != null || ucumService.validate(right) != null) {
            return null
        }

        return combineUnitsInternal(left, right, operation, state)
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun convertUnit(
        fromVal: BigDecimal,
        fromUnit: String,
        toUnit: String,
        state: State?,
    ): BigDecimal? {
        val sourceUnit = checkUnit(fromUnit)
        val destUnit = checkUnit(toUnit)

        if (sourceUnit == destUnit) {
            return fromVal
        }

        val ucumService = state?.environment?.libraryManager?.ucumService!!

        return try {
            ucumService.convert(fromVal, sourceUnit, destUnit)
        } catch (e: Exception) {
            null
        }
    }
}
