package org.opencds.cqf.cql.engine.fhir.converter

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import java.io.PrintWriter
import java.io.StringWriter
import java.math.BigDecimal
import java.util.*
import org.apache.commons.lang3.NotImplementedException
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseCoding
import org.hl7.fhir.instance.model.api.ICompositeType
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.opencds.cqf.cql.engine.exception.InvalidPrecision
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple

internal abstract class BaseFhirTypeConverter : FhirTypeConverter {
    override fun isFhirType(value: Any): Boolean {
        require(value !is Iterable<*>) { "isFhirType can not be used for Iterables" }
        return value is IBase
    }

    override fun toFhirTypes(values: Iterable<*>): MutableList<Any?> {
        val converted: MutableList<Any?> = ArrayList<Any?>()
        for (value in values) {
            if (value == null) {
                converted.add(null)
            } else if (value is Iterable<*>) {
                converted.add(toFhirTypes(value))
            } else if (isFhirType(value)) {
                converted.add(value)
            } else {
                converted.add(toFhirType(value))
            }
        }

        return converted
    }

    override fun toFhirType(value: Any?): IBase? {
        if (value == null) {
            return null
        }

        require(value !is Iterable<*>) { "use toFhirTypes(Iterable<Object>) for iterables" }

        if (value is Exception) {
            return toFhirOperationOutcome(value)
        }

        if (isFhirType(value)) {
            return value as IBase
        }

        require(isCqlType(value)) { "can't convert ${value.javaClass.name} to FHIR type" }

        when (value.javaClass.getSimpleName()) {
            "Boolean" -> return toFhirBoolean(value as Boolean)
            "Integer" -> return toFhirInteger(value as Int)
            "Long" -> return toFhirInteger64(value as Long)
            "BigDecimal" -> return toFhirDecimal(value as BigDecimal)
            "Date" -> return toFhirDate(value as Date)
            "DateTime" -> return toFhirDateTime(value as DateTime)
            "Time" -> return toFhirTime(value as Time)
            "String" -> return toFhirString(value as String)
            "Quantity" -> return toFhirQuantity(value as Quantity)
            "Ratio" -> return toFhirRatio(value as Ratio)
            "Any" -> return toFhirAny(value)
            "Code" -> return toFhirCoding(value as Code)
            "Concept" -> return toFhirCodeableConcept(value as Concept)
            "Interval" -> return toFhirInterval(value as Interval)
            "Tuple" -> return toFhirTuple(value as Tuple)
            else -> return toCqlText(value)
        }
    }

    /**
     * Determines whether the given string is a CQL calendar unit
     *
     * @param unit
     * @return true if the given unit is a CQL calendar unit
     */
    override fun isCqlCalendarUnit(unit: String?): Boolean {
        if (unit == null) {
            return false
        }

        return when (unit) {
            "milliseconds",
            "millisecond",
            "seconds",
            "second",
            "minutes",
            "minute",
            "hours",
            "hour",
            "days",
            "day",
            "weeks",
            "week",
            "months",
            "month",
            "years",
            "year" -> true
            else -> false
        }
    }

    /**
     * Converts the given CQL unit to a UCUM definite-time duration unit according to the table and
     * process defined in the CQL specification: https://cql.hl7.org/02-authorsguide.html#quantities
     *
     * @param unit
     * @return An equivalent UCUM unit for the given CQL calendar duration unit, if the input is a
     *   CQL calendar duration unit, otherwise returns the input unit.
     */
    override fun toUcumUnit(unit: String?): String? {
        if (unit == null) {
            return null
        }

        return when (unit) {
            "milliseconds",
            "millisecond" -> "ms"
            "seconds",
            "second" -> "s"
            "minutes",
            "minute" -> "min"
            "hours",
            "hour" -> "h"
            "days",
            "day" -> "d"
            "weeks",
            "week" -> "wk"
            "months",
            "month" -> "mo"
            "years",
            "year" -> "a"
            else -> unit
        }
    }

    /**
     * Converts a Ucum unit to the equivalent CQL unit according to the table defined in the CQL
     * specification: https://cql.hl7.org/02-authorsguide.html#quantities
     *
     * @param unit
     * @return A CQL calendar unit if the input unit is a Ucum definite-duration unit, otherwise,
     *   the input unit
     */
    override fun toCqlCalendarUnit(unit: String?): String? {
        return when (unit) {
            null -> null
            "ms" -> "millisecond"
            "s" -> "second"
            "min" -> "minute"
            "h" -> "hour"
            "d" -> "day"
            "wk" -> "week"
            "mo" -> "month"
            "a" -> "year"
            else -> unit
        }
    }

    override fun toFhirInterval(value: Interval?): IBase? {
        if (value == null) {
            return null
        }

        return when (getSimpleName(value.pointType!!.typeName)) {
            "Date",
            "DateTime" -> toFhirPeriod(value)
            "Quantity" -> toFhirRange(value)
            else -> toCqlText(value)
        }
    }

    override fun isCqlType(value: Any): Boolean {
        require(value !is Iterable<*>) { "isCqlType can not be used for Iterables" }
        return value is BigDecimal ||
            value is String ||
            value is Int ||
            value is Boolean ||
            value is Long ||
            value is CqlType
    }

    override fun toCqlTypes(values: Iterable<*>): Iterable<Any?> {
        val converted: MutableList<Any?> = ArrayList<Any?>()
        for (value in values) {
            if (value == null) {
                converted.add(null)
            } else if (value is Iterable<*>) {
                converted.add(toCqlTypes(value))
            } else if (isCqlType(value)) {
                converted.add(value)
            } else {
                converted.add(toCqlType(value))
            }
        }

        return converted
    }

    override fun toCqlType(value: Any?): Any? {
        if (value == null) {
            return null
        }

        require(value !is Iterable<*>) { "use toCqlTypes(Iterable<Object>) for iterables" }

        if (isCqlType(value)) {
            return value
        }

        require(isFhirType(value)) { "can't convert ${value.javaClass.name} to CQL type" }

        when (value.javaClass.getSimpleName()) {
            "IdType" -> return toCqlId(value as IIdType)
            "BooleanType" -> return toCqlBoolean(value.asIPrimitive())
            "IntegerType" -> return toCqlInteger(value.asIPrimitive())
            "Integer64Type" -> return toCqlLong(value.asIPrimitive())
            "DecimalType" -> return toCqlDecimal(value.asIPrimitive())
            "DateType" -> return toCqlDate(value.asIPrimitive())
            "InstantType",
            "DateTimeType" -> return toCqlDateTime(value.asIPrimitive())
            "TimeType" -> return toCqlTime(value.asIPrimitive())
            "StringType" -> return toCqlString(value.asIPrimitive())
            "Quantity" -> return toCqlQuantity(value as ICompositeType)
            "Ratio" -> return toCqlRatio(value as ICompositeType)
            "Coding" -> return toCqlCode(value as IBaseCoding)
            "CodeableConcept" -> return toCqlConcept(value as ICompositeType)
            "Period",
            "Range" -> return toCqlInterval(value as ICompositeType)
            else ->
                throw IllegalArgumentException(
                    "missing case statement for: ${value.javaClass.name}"
                )
        }
    }

    private fun <T> Any?.asIPrimitive(): IPrimitiveType<T>? {
        @Suppress("UNCHECKED_CAST")
        return this as? IPrimitiveType<T>
    }

    override fun toCqlId(value: IIdType?): String? {
        if (value == null) {
            return null
        }

        return value.idPart
    }

    override fun toCqlBoolean(value: IPrimitiveType<Boolean>?): Boolean? {
        if (value == null) {
            return null
        }

        return value.getValue()
    }

    override fun toCqlInteger(value: IPrimitiveType<Int>?): Int? {
        if (value == null) {
            return null
        }

        return value.getValue()
    }

    override fun toCqlLong(value: IPrimitiveType<Long>?): Long? {
        if (value == null) {
            return null
        }

        return value.getValue()
    }

    override fun toCqlDecimal(value: IPrimitiveType<BigDecimal>?): BigDecimal? {
        if (value == null) {
            return null
        }

        return value.getValue()
    }

    override fun toCqlTime(value: IPrimitiveType<String>?): Time? {
        if (value == null) {
            return null
        }

        return Time(value.getValue())
    }

    override fun toCqlString(value: IPrimitiveType<String>?): String? {
        if (value == null) {
            return null
        }

        return value.getValue()
    }

    override fun toCqlTuple(value: IBase?): Tuple? {
        if (value == null) {
            return null
        }

        throw NotImplementedException("toCqlTuple is not yet implemented")
    }

    protected fun getSimpleName(typeName: String): String? {
        val nameParts: Array<String?> =
            typeName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return nameParts[nameParts.size - 1]
    }

    protected fun toTime(calendar: Calendar, calendarConstant: Int): Time {
        when (calendarConstant) {
            Calendar.HOUR -> return Time(calendar.get(Calendar.HOUR))
            Calendar.MINUTE ->
                return Time(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE))

            Calendar.SECOND ->
                return Time(
                    calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND),
                )

            Calendar.MILLISECOND ->
                return Time(
                    calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND),
                    calendar.get(Calendar.MILLISECOND),
                )

            else -> throw InvalidPrecision("Invalid temporal precision $calendarConstant")
        }
    }

    protected fun toDateTime(calendar: Calendar, calendarConstant: Int): DateTime {
        val tz =
            if (calendar.getTimeZone() == null) TimeZone.getDefault() else calendar.getTimeZone()
        val zoneOffset = tz.toZoneId().rules.getStandardOffset(calendar.toInstant())
        when (calendarConstant) {
            Calendar.YEAR ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                )
            Calendar.MONTH ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                )

            Calendar.DAY_OF_MONTH ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                )

            Calendar.HOUR_OF_DAY ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                )

            Calendar.MINUTE ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                )

            Calendar.SECOND ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND),
                )

            Calendar.MILLISECOND ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND),
                    calendar.get(Calendar.MILLISECOND),
                )

            else -> throw InvalidPrecision("Invalid temporal precision $calendarConstant")
        }
    }

    protected fun toDate(calendar: Calendar, calendarConstant: Int): Date {
        return when (calendarConstant) {
            Calendar.YEAR -> Date(calendar.get(Calendar.YEAR))
            Calendar.MONTH -> Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)

            Calendar.DAY_OF_MONTH ->
                Date(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                )

            else -> throw InvalidPrecision("Invalid temporal precision $calendarConstant")
        }
    }

    protected fun toFhirPrecision(precision: Precision): TemporalPrecisionEnum {
        val name =
            when (precision) {
                Precision.WEEK,
                Precision.HOUR,
                Precision.MINUTE -> TemporalPrecisionEnum.DAY.name
                Precision.MILLISECOND -> TemporalPrecisionEnum.MILLI.name
                else -> precision.name
            }
        return TemporalPrecisionEnum.valueOf(name)
    }

    protected fun getStackTraceAsString(exception: Exception): String {
        val sw = StringWriter()
        PrintWriter(sw).use { pw -> exception.printStackTrace(pw) }
        return sw.toString()
    }
}
