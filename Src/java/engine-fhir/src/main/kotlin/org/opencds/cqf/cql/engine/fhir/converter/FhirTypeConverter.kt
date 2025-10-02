package org.opencds.cqf.cql.engine.fhir.converter

import java.math.BigDecimal
import java.util.*
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseCoding
import org.hl7.fhir.instance.model.api.IBaseDatatype
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome
import org.hl7.fhir.instance.model.api.ICompositeType
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple

/**
 * Provides functions for converting from CQL-to-FHIR and vice versa. The return types on the
 * functions represent the most derived common type or interface across all FHIR versions. The
 * implementations of this interface are expected to return the appropriate version specific
 * structure for a given conversion (e.g. the interface for toFhirBoolean is defined as
 * IPrimitiveType&lt;Boolean&gt; but it will return n dstu3.model.BooleanType or
 * r4.model.BooleanType and so on).
 */
interface FhirTypeConverter {
    // CQL-to-FHIR conversions
    /**
     * Tests if an Object is a FHIR structure
     *
     * @param value the value to test
     * @return true if value is a FHIR structure, false otherwise
     * @throws NullPointerException if value is null
     */
    fun isFhirType(value: Any): Boolean

    /**
     * Converts an Object to a FHIR structure.
     *
     * @param value the value to convert
     * @return a FHIR structure
     * @throws IllegalArgumentException is value is an Iterable
     */
    fun toFhirType(value: Any?): IBase?

    /**
     * Converts an iterable of Objects to FHIR structures. Preserves ordering, nulls, and sublist
     * hierarchy
     *
     * @param values an Iterable containing CQL structures, nulls, or sublists
     * @return n List containing FHIR types, nulls, and sublists
     */
    fun toFhirTypes(values: Iterable<*>): MutableList<Any?>?

    /**
     * Converts an Object to the equivalent CQL representation. This is used for arbitrary types
     * that do not have well-defined FHIR mappings, such as CQL Integer Intervals.
     *
     * The default implementation should use the CQL ToString operator and embed add the cqf-cqlText
     * extension to the FHIR structure.
     *
     * @param value the value to convert
     * @return a FHIR String
     * @return
     */
    fun toCqlText(value: Any?): IBaseDatatype?

    /**
     * Converts an Exception to a FHIR OperationOutcome.
     *
     * The default implementation should create an OperationOutcome with an issue of type
     * "exception" and severity "error", and include the exception message and stack trace in the
     * details.
     *
     * @param exception
     * @return a FHIR OperationOutcome
     */
    fun toFhirOperationOutcome(exception: Exception?): IBaseOperationOutcome?

    /**
     * Converts a String to a FHIR Id
     *
     * @param value the value to convert
     * @return a FHIR Id
     */
    fun toFhirId(value: String?): IIdType?

    /**
     * Converts a Boolean to a FHIR Boolean
     *
     * @param value the value to convert
     * @return a FHIR Boolean
     */
    fun toFhirBoolean(value: Boolean?): IPrimitiveType<Boolean>?

    /**
     * Converts an Integer to a FHIR Integer
     *
     * @param value the value to convert
     * @return a FHIR Integer
     */
    fun toFhirInteger(value: Int?): IPrimitiveType<Int>?

    /**
     * Converts a Long to a FHIR Integer64
     *
     * @param value the value to convert
     * @return a FHIR Integer64
     */
    fun toFhirInteger64(value: Long?): IPrimitiveType<Long>?

    /**
     * Converts a BigDecimal to a FHIR Decimal
     *
     * @param value the value to convert
     * @return a FHIR Decimal
     */
    fun toFhirDecimal(value: BigDecimal?): IPrimitiveType<BigDecimal>?

    /**
     * Converts a CQL Date to a FHIR Date
     *
     * @param value the value to convert
     * @return a FHIR Date
     */
    fun toFhirDate(value: org.opencds.cqf.cql.engine.runtime.Date?): IPrimitiveType<Date>?

    /**
     * Converts a CQL DateTime to a FHIR DateTime
     *
     * @param value the value to convert
     * @return a FHIR DateTime
     */
    fun toFhirDateTime(value: DateTime?): IPrimitiveType<Date>?

    /**
     * Converts a CQL Time to a FHIR Time
     *
     * @param value the value to convert
     * @return a FHIR Time
     */
    fun toFhirTime(value: Time?): IPrimitiveType<String>?

    /**
     * Converts a String to a FHIR String
     *
     * @param value the value to convert
     * @return a FHIR String
     */
    fun toFhirString(value: String?): IPrimitiveType<String>?

    /**
     * Converts a CQL Quantity to a FHIR Quantity
     *
     * @param value the value to convert
     * @return a FHIR Quantity
     */
    fun toFhirQuantity(value: Quantity?): ICompositeType?

    /**
     * Determines whether the given string is a CQL calendar unit
     *
     * @param unit
     * @return true if the given unit is a CQL calendar unit
     */
    fun isCqlCalendarUnit(unit: String?): Boolean

    /**
     * Converts the given CQL unit to a UCUM definite-time duration unit according to the table and
     * process defined in the CQL specification: https://cql.hl7.org/02-authorsguide.html#quantities
     *
     * @param unit
     * @return An equivalent UCUM unit for the given CQL calendar duration unit, if the input is a
     *   CQL calendar duration unit, otherwise returns the input unit.
     */
    fun toUcumUnit(unit: String?): String?

    /**
     * Converts an Ucum unit to the equivalent CQL unit according to the table defined in the CQL
     * specification: https://cql.hl7.org/02-authorsguide.html#quantities
     *
     * @param unit
     * @return A CQL calendar unit if the input unit is an Ucum definite-duration unit, otherwise,
     *   the input unit
     */
    fun toCqlCalendarUnit(unit: String?): String?

    /**
     * Converts a CQL Ratio to a FHIR Ratio
     *
     * @param value the value to convert
     * @return a FHIR Ratio
     */
    fun toFhirRatio(value: Ratio?): ICompositeType?

    /**
     * Converts a CQL Any to a FHIR Any
     *
     * @param value the value to convert
     * @return a FHIR Any
     */
    fun toFhirAny(value: Any?): IBase?

    /**
     * Converts a CQL Code to a FHIR Coding
     *
     * @param value the value to convert
     * @return a FHIR Coding
     */
    fun toFhirCoding(value: Code?): IBaseCoding?

    /**
     * Converts a CQL Concept to a FHIR CodeableConcept
     *
     * @param value the value to convert
     * @return a FHIR CodeableConcept
     */
    fun toFhirCodeableConcept(value: Concept?): ICompositeType?

    /**
     * Converts a CQL Interval to a FHIR Period
     *
     * @param value a Date or DateTime Interval
     * @return a FHIR Period
     */
    fun toFhirPeriod(value: Interval?): ICompositeType?

    /**
     * Converts a CQL Interval to a FHIR Range
     *
     * @param value a Quantity Interval
     * @return a FHIR Range
     */
    fun toFhirRange(value: Interval?): ICompositeType?

    /**
     * Converts a CQL Interval to FHIR Range or Period
     *
     * @param value a Quantity, Date, or DateTime interval
     * @return A FHIR Range or Period
     */
    fun toFhirInterval(value: Interval?): IBase?

    /**
     * Converts a CQL Tuple to a FHIR Structure
     *
     * @param value the value to convert
     * @return a FHIR Structure
     */
    fun toFhirTuple(value: Tuple?): IBase?

    // FHIR-to-CQL conversions
    /**
     * Tests if an Object is a CQL type
     *
     * @param value the value to convert
     * @return true if value is a CQL type, false otherwise
     * @throws NullPointerException if value is null
     */
    fun isCqlType(value: Any): Boolean

    /**
     * Converts an Object to a CQL type.
     *
     * @param value the value to convert a FHIR structure
     * @return a CQL type
     * @throws IllegalArgumentException is value is an Iterable
     */
    fun toCqlType(value: Any?): Any?

    /**
     * Converts an iterable of Objects to CQL types. Preserves ordering, nulls, and sublist
     * hierarchy
     *
     * @param values the values to convert an Iterable containing FHIR structures, nulls, or
     *   sublists
     * @return an Iterable containing CQL types, nulls, and sublists
     */
    fun toCqlTypes(values: Iterable<*>): Iterable<Any?>?

    /**
     * Converts a FHIR Id to a CQL String
     *
     * @param value the value to convert
     * @return a String
     */
    fun toCqlId(value: IIdType?): String?

    /**
     * Converts a FHIR Boolean to a CQL Boolean
     *
     * @param value the value to convert
     * @return a Boolean
     */
    fun toCqlBoolean(value: IPrimitiveType<Boolean>?): Boolean?

    /**
     * Converts a FHIR Integer to a CQL Integer
     *
     * @param value the value to convert
     * @return an Integer
     */
    fun toCqlInteger(value: IPrimitiveType<Int>?): Int?

    /**
     * Converts a FHIR Integer64 to a CQL Long
     *
     * @param value the value to convert
     * @return a Long
     */
    fun toCqlLong(value: IPrimitiveType<Long>?): Long?

    /**
     * Converts a FHIR Decimal to a CQL Decimal
     *
     * @param value the value to convert
     * @return a BigDecimal
     */
    fun toCqlDecimal(value: IPrimitiveType<BigDecimal>?): BigDecimal?

    /**
     * Converts a FHIR Date to a CQL Date
     *
     * @param value the value to convert
     * @return a CQL Date
     * @throws IllegalArgumentException if value is not a Date
     */
    fun toCqlDate(value: IPrimitiveType<Date>?): org.opencds.cqf.cql.engine.runtime.Date?

    /**
     * Converts a FHIR DateTime to a CQL DateTime
     *
     * @param value the value to convert
     * @return a CQL DateTime
     * @throws IllegalArgumentException if value is not a DateTime
     */
    fun toCqlDateTime(value: IPrimitiveType<Date>?): DateTime?

    /**
     * Converts a FHIR DateTime, Date, or Instance to a CQL BaseTemporal
     *
     * @param value the value to convert
     * @return a CQL BaseTemporal
     * @throws IllegalArgumentException if value is not a DateTime, Date, or Instant
     */
    fun toCqlTemporal(value: IPrimitiveType<Date>?): BaseTemporal?

    /**
     * Converts a FHIR Time to a CQL Time
     *
     * @param value the value to convert
     * @return a CQL Time
     * @throws IllegalArgumentException if value is not a Time
     */
    fun toCqlTime(value: IPrimitiveType<String>?): Time?

    /**
     * Converts a FHIR String to a CQL String
     *
     * @param value the value to convert
     * @return a String
     */
    fun toCqlString(value: IPrimitiveType<String>?): String?

    /**
     * Converts a FHIR Quantity to a CQL Quantity
     *
     * @param value the value to convert
     * @return a CQL Quantity
     * @throws IllegalArgumentException if value is not a Quantity
     */
    fun toCqlQuantity(value: ICompositeType?): Quantity?

    /**
     * Converts a FHIR Ratio to a CQL Ratio
     *
     * @param value the value to convert
     * @return a CQL Ratio
     * @throws IllegalArgumentException if value is not a Ratio
     */
    fun toCqlRatio(value: ICompositeType?): Ratio?

    /**
     * Converts a FHIR Any to a CQL Any
     *
     * @param value the value to convert
     * @return a CQL Any
     */
    fun toCqlAny(value: IBase?): Any?

    /**
     * Converts a FHIR Coding to a CQL Code
     *
     * @param value the value to convert
     * @return a CQL Code
     */
    fun toCqlCode(value: IBaseCoding?): Code?

    /**
     * Converts a FHIR CodeableConcept to a CQL Concept
     *
     * @param value the value to convert
     * @return a CQL Concept
     * @throws IllegalArgumentException if value is not a CodeableConcept
     */
    fun toCqlConcept(value: ICompositeType?): Concept?

    /**
     * Converts a FHIR Range or Period to a CQL Interval
     *
     * @param value the value to convert
     * @return a CQL Interval
     * @throws IllegalArgumentException if value is not a Range or Period
     */
    fun toCqlInterval(value: ICompositeType?): Interval?

    /**
     * Converts a FHIR Structure to a CQL Tuple
     *
     * @param value the value to convert
     * @return a CQL Tuple
     */
    fun toCqlTuple(value: IBase?): Tuple?

    companion object {
        const val EMPTY_LIST_EXT_URL: String =
            "http://hl7.org/fhir/StructureDefinition/cqf-isEmptyList"
        const val EMPTY_TUPLE_EXT_URL: String =
            "http://hl7.org/fhir/StructureDefinition/cqf-isEmptyTuple"
        const val DATA_ABSENT_REASON_EXT_URL: String =
            "http://hl7.org/fhir/StructureDefinition/data-absent-reason"
        const val DATA_ABSENT_REASON_UNKNOWN_CODE: String = "unknown"
        const val CQL_TYPE_EXT_URL: String = "http://hl7.org/fhir/StructureDefinition/cqf-cqlType"

        // Stacktrace of an exception that occurred during CQL evaluation, as the native platform
        // represents it (e.g. Java)
        const val NATIVE_STACK_TRACE_EXT_URL: String =
            "http://hl7.org/fhir/StructureDefinition/cqf-nativeStackTrace"

        // The CQL representation of a FHIR structure.
        const val CQL_TEXT_EXT_URL: String = "http://hl7.org/fhir/StructureDefinition/cqf-cqlText"
    }
}
