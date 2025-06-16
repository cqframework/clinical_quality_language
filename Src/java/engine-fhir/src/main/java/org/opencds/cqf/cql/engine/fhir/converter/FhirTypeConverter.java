package org.opencds.cqf.cql.engine.fhir.converter;

import java.math.BigDecimal;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Ratio;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.opencds.cqf.cql.engine.runtime.Tuple;

/**
 * Provides functions for converting from CQL-to-FHIR and vice versa. The return
 * types on the functions represent the most derived common type or interface
 * across all FHIR versions. The implementations of this interface are expected
 * to return the appropriate version specific structure for a given conversion
 * (e.g. the interface for toFhirBoolean is defined as
 * IPrimitiveType&lt;Boolean&gt; but it will return an dstu3.model.BooleanType
 * or r4.model.BooleanType and so on).
 */
public interface FhirTypeConverter {

    static final String EMPTY_LIST_EXT_URL = "http://hl7.org/fhir/StructureDefinition/cqf-isEmptyList";
    static final String EMPTY_TUPLE_EXT_URL = "http://hl7.org/fhir/StructureDefinition/cqf-isEmptyTuple";
    static final String DATA_ABSENT_REASON_EXT_URL = "http://hl7.org/fhir/StructureDefinition/data-absent-reason";
    static final String DATA_ABSENT_REASON_UNKNOWN_CODE = "unknown";
    static final String CQL_TYPE_EXT_URL = "http://hl7.org/fhir/StructureDefinition/cqf-cqlType";

    // Stacktrace of an exception that occurred during CQL evaluation, as the native platform represents it (e.g. Java)
    static final String NATIVE_STACK_TRACE_EXT_URL = "http://hl7.org/fhir/StructureDefinition/cqf-nativeStackTrace";

    // The CQL representation of a FHIR structure.
    static final String CQL_TEXT_EXT_URL = "http://hl7.org/fhir/StructureDefinition/cqf-cqlText";

    // CQL-to-FHIR conversions

    /**
     * Tests if an Object is a FHIR structure
     *
     * @param value the value to test
     * @return true if value is a FHIR structure, false otherwise
     * @throws NullPointerException if value is null
     */
    public boolean isFhirType(Object value);

    /**
     * Converts an Object to a FHIR structure.
     *
     * @param value the value to convert
     * @return a FHIR structure
     * @throws IllegalArgumentException is value is an Iterable
     */
    public IBase toFhirType(Object value);

    /**
     * Converts an iterable of Objects to FHIR structures. Preserves ordering,
     * nulls, and sublist hierarchy
     *
     * @param values an Iterable containing CQL structures, nulls, or sublists
     * @return an List containing FHIR types, nulls, and sublists
     */
    public List<Object> toFhirTypes(Iterable<?> values);

    /**
     * Converts an Object to the equivalent CQL representation. This is used for arbitrary
     * types that do not have well-defined FHIR mappings, such as CQL Integer Intervals.
     *
     * The default implementation should use the CQL ToString operator and
     * embed add the cqf-cqlText extension to the FHIR structure.
     *
     * @param value the value to convert
     * @return a FHIR String
     *
     * @return
     */
    public IBaseDatatype toCqlText(Object value);

    /**
     * Converts an Exception to a FHIR OperationOutcome.
     *
     * The default implementation should create an OperationOutcome
     * with an issue of type "exception" and severity "error", and
     * include the exception message and stack trace in the details.
     *
     * @param exception
     * @return a FHIR OperationOutcome
     */
    public IBaseOperationOutcome toFhirOperationOutcome(Exception exception);

    /**
     * Converts a String to a FHIR Id
     *
     * @param value the value to convert
     * @return a FHIR Id
     */
    public IIdType toFhirId(String value);

    /**
     * Converts a Boolean to a FHIR Boolean
     *
     * @param value the value to convert
     * @return a FHIR Boolean
     */
    public IPrimitiveType<Boolean> toFhirBoolean(Boolean value);

    /**
     * Converts an Integer to a FHIR Integer
     *
     * @param value the value to convert
     * @return a FHIR Integer
     */
    public IPrimitiveType<Integer> toFhirInteger(Integer value);

    /**
     * Converts a Long to a FHIR Integer64
     * @param value the value to convert
     * @return a FHIR Integer64
     */
    public IPrimitiveType<Long> toFhirInteger64(Long value);

    /**
     * Converts a BigDecimal to a FHIR Decimal
     *
     * @param value the value to convert
     * @return a FHIR Decimal
     */
    public IPrimitiveType<BigDecimal> toFhirDecimal(BigDecimal value);

    /**
     * Converts a CQL Date to a FHIR Date
     *
     * @param value the value to convert
     * @return a FHIR Date
     */
    public IPrimitiveType<java.util.Date> toFhirDate(Date value);

    /**
     * Converts a CQL DateTime to a FHIR DateTime
     *
     * @param value the value to convert
     * @return a FHIR DateTime
     */
    public IPrimitiveType<java.util.Date> toFhirDateTime(DateTime value);

    /**
     * Converts a CQL Time to a FHIR Time
     *
     * @param value the value to convert
     * @return a FHIR Time
     */
    public IPrimitiveType<String> toFhirTime(Time value);

    /**
     * Converts a String to a FHIR String
     *
     * @param value the value to convert
     * @return a FHIR String
     */
    public IPrimitiveType<String> toFhirString(String value);

    /**
     * Converts a CQL Quantity to a FHIR Quantity
     *
     * @param value the value to convert
     * @return a FHIR Quantity
     */
    public ICompositeType toFhirQuantity(Quantity value);

    /**
     * Determines whether the given string is a CQL calendar unit
     * @param unit
     * @return true if the given unit is a CQL calendar unit
     */
    public boolean isCqlCalendarUnit(String unit);

    /**
     * Converts the given CQL unit to a UCUM definite-time duration unit according to the table
     * and process defined in the CQL specification: https://cql.hl7.org/02-authorsguide.html#quantities
     * @param unit
     * @return An equivalent UCUM unit for the given CQL calendar duration unit, if the input is a
     * CQL calendar duration unit, otherwise returns the input unit.
     */
    public String toUcumUnit(String unit);

    /**
     * Converts a Ucum unit to the equivalent CQL unit according to the table defined in the
     * CQL specification: https://cql.hl7.org/02-authorsguide.html#quantities
     * @param unit
     * @return A CQL calendar unit if the input unit is a Ucum definite-duration unit, otherwise, the input unit
     */
    public String toCqlCalendarUnit(String unit);

    /**
     * Converts a CQL Ratio to a FHIR Ratio
     *
     * @param value the value to convert
     * @return a FHIR Ratio
     */
    public ICompositeType toFhirRatio(Ratio value);

    /**
     * Converts a CQL Any to a FHIR Any
     *
     * @param value the value to convert
     * @return a FHIR Any
     */
    public IBase toFhirAny(Object value);

    /**
     * Converts a CQL Code to a FHIR Coding
     *
     * @param value the value to convert
     * @return a FHIR Coding
     */
    public IBaseCoding toFhirCoding(Code value);

    /**
     * Converts a CQL Concept to a FHIR CodeableConcept
     *
     * @param value the value to convert
     * @return a FHIR CodeableConcept
     */
    public ICompositeType toFhirCodeableConcept(Concept value);

    /**
     * Converts a CQL Interval to a FHIR Period
     *
     * @param value a Date or DateTime Interval
     * @return a FHIR Period
     */
    public ICompositeType toFhirPeriod(Interval value);

    /**
     * Converts a CQL Interval to a FHIR Range
     *
     * @param value a Quantity Interval
     * @return a FHIR Range
     */
    public ICompositeType toFhirRange(Interval value);

    /**
     * Converts a CQL Interval to FHIR Range or Period
     *
     * @param value a Quantity, Date, or DateTime interval
     * @return A FHIR Range or Period
     */
    public IBase toFhirInterval(Interval value);

    /**
     * Converts a CQL Tuple to a FHIR Structure
     *
     * @param value the value to convert
     * @return a FHIR Structure
     */
    public IBase toFhirTuple(Tuple value);

    // FHIR-to-CQL conversions

    /**
     * Tests if an Object is a CQL type
     *
     * @param value the value to convert
     * @return true if value is a CQL type, false otherwise
     * @throws NullPointerException if value is null
     */
    public boolean isCqlType(Object value);

    /**
     * Converts an Object to a CQL type.
     *
     * @param value the value to convert a FHIR structure
     * @return a CQL type
     * @throws IllegalArgumentException is value is an Iterable
     */
    public Object toCqlType(Object value);

    /**
     * Converts an iterable of Objects to CQL types. Preserves ordering, nulls, and
     * sublist hierarchy
     *
     * @param values the values to convert an Iterable containing FHIR structures,
     *              nulls, or sublists
     * @return an Iterable containing CQL types, nulls, and sublists
     */
    public Iterable<Object> toCqlTypes(Iterable<?> values);

    /**
     * Converts a FHIR Id to a CQL String
     *
     * @param value the value to convert
     * @return a String
     */
    public String toCqlId(IIdType value);

    /**
     * Converts a FHIR Boolean to a CQL Boolean
     *
     * @param value the value to convert
     * @return a Boolean
     */
    public Boolean toCqlBoolean(IPrimitiveType<Boolean> value);

    /**
     * Converts a FHIR Integer to a CQL Integer
     *
     * @param value the value to convert
     * @return an Integer
     */
    public Integer toCqlInteger(IPrimitiveType<Integer> value);

    /**
     * Converts a FHIR Integer64 to a CQL Long
     * @param value the value to convert
     * @return a Long
     */
    public Long toCqlLong(IPrimitiveType<Long> value);

    /**
     * Converts a FHIR Decimal to a CQL Decimal
     *
     * @param value the value to convert
     * @return a BigDecimal
     */
    public BigDecimal toCqlDecimal(IPrimitiveType<BigDecimal> value);

    /**
     * Converts a FHIR Date to a CQL Date
     *
     * @param value the value to convert
     * @return a CQL Date
     * @throws IllegalArgumentException if value is not a Date
     */
    public Date toCqlDate(IPrimitiveType<java.util.Date> value);

    /**
     * Converts a FHIR DateTime to a CQL DateTime
     *
     * @param value the value to convert
     * @return a CQL DateTime
     * @throws IllegalArgumentException if value is not a DateTime
     */
    public DateTime toCqlDateTime(IPrimitiveType<java.util.Date> value);

    /**
     * Converts a FHIR DateTime, Date, or Instance to a CQL BaseTemporal
     *
     * @param value the value to convert
     * @return a CQL BaseTemporal
     * @throws IllegalArgumentException if value is not a DateTime, Date, or Instant
     */
    public BaseTemporal toCqlTemporal(IPrimitiveType<java.util.Date> value);

    /**
     * Converts a FHIR Time to a CQL Time
     *
     * @param value the value to convert
     * @return a CQL Time
     * @throws IllegalArgumentException if value is not a Time
     */
    public Time toCqlTime(IPrimitiveType<String> value);

    /**
     * Converts a FHIR String to a CQL String
     *
     * @param value the value to convert
     * @return a String
     */
    public String toCqlString(IPrimitiveType<String> value);

    /**
     * Converts a FHIR Quantity to a CQL Quantity
     *
     * @param value the value to convert
     * @return a CQL Quantity
     * @throws IllegalArgumentException if value is not a Quantity
     */
    public Quantity toCqlQuantity(ICompositeType value);

    /**
     * Converts a FHIR Ratio to a CQL Ratio
     *
     * @param value the value to convert
     * @return a CQL Ratio
     * @throws IllegalArgumentException if value is not a Ratio
     */
    public Ratio toCqlRatio(ICompositeType value);

    /**
     * Converts a FHIR Any to a CQL Any
     *
     * @param value the value to convert
     * @return a CQL Any
     */
    public Object toCqlAny(IBase value);

    /**
     * Converts a FHIR Coding to a CQL Code
     *
     * @param value the value to convert
     * @return a CQL Code
     */
    public Code toCqlCode(IBaseCoding value);

    /**
     * Converts a FHIR CodeableConcept to a CQL Concept
     *
     * @param value the value to convert
     * @return a CQL Concept
     * @throws IllegalArgumentException if value is not a CodeableConcept
     */
    public Concept toCqlConcept(ICompositeType value);

    /**
     * Converts a FHIR Range or Period to a CQL Interval
     *
     * @param value the value to convert
     * @return a CQL Interval
     * @throws IllegalArgumentException if value is not a Range or Period
     */
    public Interval toCqlInterval(ICompositeType value);

    /**
     * Converts a FHIR Structure to a CQL Tuple
     *
     * @param value the value to convert
     * @return a CQL Tuple
     */
    public Tuple toCqlTuple(IBase value);
}
