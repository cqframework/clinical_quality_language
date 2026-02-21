package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal

class Quantity : CqlType, Comparable<Quantity> {
    var value: BigDecimal? = BigDecimal("0.0")

    fun withValue(value: BigDecimal?): Quantity {
        this.value = value
        return this
    }

    var unit: String? = DEFAULT_UNIT

    fun withUnit(unit: String?): Quantity {
        this.unit = unit
        return this
    }

    fun withDefaultUnit(): Quantity {
        this.unit = DEFAULT_UNIT
        return this
    }

    override fun compareTo(other: Quantity): Int {
        if (unitsEqual(this.unit, other.unit)) {
            return this.value!!.compareTo(other.value!!)
        }
        return -1
    }

    fun nullableCompareTo(other: Quantity): Int? {
        if (unitsEqual(this.unit, other.unit)) {
            return this.value!!.compareTo(other.value!!)
        }
        return null
    }

    override fun toString(): String {
        return "${this.value} '${this.unit}'"
    }

    companion object {
        private const val DEFAULT_UNIT = "1"

        @JvmStatic
        fun isDefaultUnit(unit: String?): Boolean {
            return unit == null || unit == "" || unit == DEFAULT_UNIT
        }

        fun unitsEqual(leftUnit: String?, rightUnit: String?): Boolean {
            if (isDefaultUnit(leftUnit) && isDefaultUnit(rightUnit)) {
                return true
            }

            if (isDefaultUnit(leftUnit)) {
                return false
            }

            when (leftUnit) {
                "year",
                "years" -> return "year" == rightUnit || "years" == rightUnit
                "month",
                "months" -> return "month" == rightUnit || "months" == rightUnit
                "week",
                "weeks",
                "wk" -> return "week" == rightUnit || "weeks" == rightUnit || "wk" == rightUnit
                "day",
                "days",
                "d" -> return "day" == rightUnit || "days" == rightUnit || "d" == rightUnit
                "hour",
                "hours",
                "h" -> return "hour" == rightUnit || "hours" == rightUnit || "h" == rightUnit
                "minute",
                "minutes",
                "min" ->
                    return "minute" == rightUnit || "minutes" == rightUnit || "min" == rightUnit
                "second",
                "seconds",
                "s" -> return "second" == rightUnit || "seconds" == rightUnit || "s" == rightUnit
                "millisecond",
                "milliseconds",
                "ms" ->
                    return "millisecond" == rightUnit ||
                        "milliseconds" == rightUnit ||
                        "ms" == rightUnit
                else -> return leftUnit == rightUnit
            }
        }

        fun unitsEquivalent(leftUnit: String?, rightUnit: String?): Boolean {
            if (isDefaultUnit(leftUnit) && isDefaultUnit(rightUnit)) {
                return true
            }

            if (isDefaultUnit(leftUnit)) {
                return false
            }

            when (leftUnit) {
                "year",
                "years",
                "a" -> return "year" == rightUnit || "years" == rightUnit || "a" == rightUnit
                "month",
                "months",
                "mo" -> return "month" == rightUnit || "months" == rightUnit || "mo" == rightUnit
                "week",
                "weeks",
                "wk" -> return "week" == rightUnit || "weeks" == rightUnit || "wk" == rightUnit
                "day",
                "days",
                "d" -> return "day" == rightUnit || "days" == rightUnit || "d" == rightUnit
                "hour",
                "hours",
                "h" -> return "hour" == rightUnit || "hours" == rightUnit || "h" == rightUnit
                "minute",
                "minutes",
                "min" ->
                    return "minute" == rightUnit || "minutes" == rightUnit || "min" == rightUnit
                "second",
                "seconds",
                "s" -> return "second" == rightUnit || "seconds" == rightUnit || "s" == rightUnit
                "millisecond",
                "milliseconds",
                "ms" ->
                    return "millisecond" == rightUnit ||
                        "milliseconds" == rightUnit ||
                        "ms" == rightUnit
                else -> return leftUnit == rightUnit
            }
        }
    }
}
