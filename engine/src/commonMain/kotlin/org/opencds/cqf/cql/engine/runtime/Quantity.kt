package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmStatic
import org.cqframework.cql.cql2elm.StringEscapeUtils.escapeCql
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
class Quantity(
    val value: BigDecimal? = BigDecimal("0.0"),
    val unit: kotlin.String? = DEFAULT_UNIT,
) : StructuredValue, NamedTypeValue, Comparable<Quantity> {
    override val type = quantityTypeName

    override val elements = mapOf("value" to value?.toCqlDecimal(), "unit" to unit?.toCqlString())

    fun withValue(value: BigDecimal?): Quantity {
        return Quantity(value, unit)
    }

    fun withUnit(unit: kotlin.String?): Quantity {
        return Quantity(value, unit)
    }

    fun withDefaultUnit(): Quantity {
        return Quantity(value, DEFAULT_UNIT)
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

    private val hash: Int by lazy { listOf(value, unit).hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Quantity && value == other.value && unit == other.unit)

    override fun toString(): kotlin.String {
        return this.value?.toPlainString() + " " + this.unit?.let { "'${escapeCql(it)}'" }
    }

    companion object {
        private const val DEFAULT_UNIT = "1"

        @JvmStatic
        fun isDefaultUnit(unit: kotlin.String?): kotlin.Boolean {
            return unit == null || unit == "" || unit == DEFAULT_UNIT
        }

        /**
         * Normalizes a CQL calendar-duration keyword (e.g. "day"/"days") to its UCUM code (e.g.
         * "d") so the value can be handed to the UCUM service. Units that are not calendar keywords
         * are returned unchanged.
         */
        @JvmStatic
        fun toUcumUnit(unit: kotlin.String): kotlin.String {
            return when (unit) {
                "year",
                "years" -> "a"
                "month",
                "months" -> "mo"
                "week",
                "weeks" -> "wk"
                "day",
                "days" -> "d"
                "hour",
                "hours" -> "h"
                "minute",
                "minutes" -> "min"
                "second",
                "seconds" -> "s"
                "millisecond",
                "milliseconds" -> "ms"
                else -> unit
            }
        }

        fun unitsEqual(leftUnit: kotlin.String?, rightUnit: kotlin.String?): kotlin.Boolean {
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

        fun unitsEquivalent(leftUnit: kotlin.String?, rightUnit: kotlin.String?): kotlin.Boolean {
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
