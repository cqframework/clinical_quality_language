package org.opencds.cqf.cql.engine.fhir.converter

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors
import org.apache.commons.lang3.NotImplementedException
import org.hl7.fhir.dstu2.model.BaseDateTimeType
import org.hl7.fhir.dstu2.model.BooleanType
import org.hl7.fhir.dstu2.model.CodeType
import org.hl7.fhir.dstu2.model.CodeableConcept
import org.hl7.fhir.dstu2.model.Coding
import org.hl7.fhir.dstu2.model.DateTimeType
import org.hl7.fhir.dstu2.model.DateType
import org.hl7.fhir.dstu2.model.DecimalType
import org.hl7.fhir.dstu2.model.IdType
import org.hl7.fhir.dstu2.model.IntegerType
import org.hl7.fhir.dstu2.model.OperationOutcome
import org.hl7.fhir.dstu2.model.Parameters
import org.hl7.fhir.dstu2.model.Period
import org.hl7.fhir.dstu2.model.Range
import org.hl7.fhir.dstu2.model.Resource
import org.hl7.fhir.dstu2.model.SimpleQuantity
import org.hl7.fhir.dstu2.model.StringType
import org.hl7.fhir.dstu2.model.TimeType
import org.hl7.fhir.dstu2.model.Type
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseCoding
import org.hl7.fhir.instance.model.api.IBaseDatatype
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome
import org.hl7.fhir.instance.model.api.ICompositeType
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.opencds.cqf.cql.engine.elm.executing.ToStringEvaluator
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverter.Companion.CQL_TEXT_EXT_URL
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverter.Companion.DATA_ABSENT_REASON_EXT_URL
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverter.Companion.DATA_ABSENT_REASON_UNKNOWN_CODE
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverter.Companion.EMPTY_LIST_EXT_URL
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverter.Companion.EMPTY_TUPLE_EXT_URL
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverter.Companion.NATIVE_STACK_TRACE_EXT_URL
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple

internal class Dstu2FhirTypeConverter : BaseFhirTypeConverter() {
    override fun toFhirId(value: String?): IIdType? {
        if (value == null) {
            return null
        }

        return IdType(value)
    }

    override fun toFhirBoolean(value: Boolean?): BooleanType? {
        if (value == null) {
            return null
        }

        return BooleanType(value)
    }

    override fun toFhirInteger(value: Int?): IPrimitiveType<Int>? {
        if (value == null) {
            return null
        }

        return IntegerType(value)
    }

    override fun toFhirInteger64(value: Long?): IPrimitiveType<Long> {
        throw IllegalArgumentException("FHIR DSTU2 does not support Long/Integer64 values")
    }

    override fun toFhirDecimal(value: BigDecimal?): IPrimitiveType<BigDecimal>? {
        if (value == null) {
            return null
        }

        return DecimalType(value)
    }

    override fun toFhirDate(
        value: org.opencds.cqf.cql.engine.runtime.Date?
    ): IPrimitiveType<Date>? {
        if (value == null) {
            return null
        }

        return DateType(value.toString())
    }

    override fun toFhirDateTime(value: DateTime?): IPrimitiveType<Date>? {
        if (value == null) {
            return null
        }

        val result = DateTimeType(value.toDateString())
        result.precision = toFhirPrecision(value.getPrecision())
        return result
    }

    override fun toFhirTime(value: Time?): IPrimitiveType<String>? {
        if (value == null) {
            return null
        }

        return TimeType(value.toString())
    }

    override fun toFhirString(value: String?): IPrimitiveType<String>? {
        if (value == null) {
            return null
        }

        return StringType(value)
    }

    override fun toFhirQuantity(value: Quantity?): ICompositeType? {
        if (value == null) {
            return null
        }

        val unit = value.unit
        val system =
            if (isCqlCalendarUnit(unit)) "http://hl7.org/fhirpath/CodeSystem/calendar-units"
            else "http://unitsofmeasure.org"
        val ucumUnit = toUcumUnit(unit)

        return org.hl7.fhir.dstu2.model
            .Quantity()
            .setSystem(system)
            .setCode(ucumUnit)
            .setValue(value.value)
            .setUnit(unit)
    }

    override fun toFhirRatio(value: Ratio?): ICompositeType? {
        if (value == null) {
            return null
        }

        return org.hl7.fhir.dstu2.model
            .Ratio()
            .setNumerator(toFhirQuantity(value.numerator) as org.hl7.fhir.dstu2.model.Quantity?)
            .setDenominator(toFhirQuantity(value.denominator) as org.hl7.fhir.dstu2.model.Quantity?)
    }

    override fun toFhirAny(value: Any?): IBase? {
        if (value == null) {
            return null
        }

        throw NotImplementedException("Unable to convert System.Any types")
    }

    override fun toFhirCoding(value: Code?): IBaseCoding? {
        if (value == null) {
            return null
        }

        val coding = Coding()
        coding.setSystem(value.system)
        coding.setCode(value.code)
        coding.setDisplay(value.display)
        coding.setVersion(value.version)
        return coding
    }

    override fun toFhirCodeableConcept(value: Concept?): ICompositeType? {
        if (value == null) {
            return null
        }

        val codeableConcept = CodeableConcept()
        codeableConcept.setText(value.display)
        if (value.codes != null) {
            for (c in value.codes) {
                codeableConcept.addCoding(toFhirCoding(c) as Coding?)
            }
        }

        return codeableConcept
    }

    override fun toFhirPeriod(value: Interval?): ICompositeType? {
        if (value == null) {
            return null
        }

        val period = Period()
        if (getSimpleName(value.pointType.getTypeName()) == "DateTime") {
            if (value.start != null) {
                period.startElement = toFhirDateTime(value.start as DateTime?) as DateTimeType?
            }

            if (value.end != null) {
                period.endElement = toFhirDateTime(value.end as DateTime?) as DateTimeType?
            }

            return period
        } else if (getSimpleName(value.pointType.getTypeName()) == "Date") {
            // TODO: This will construct DateTimeType values in FHIR with the system timezone id,
            // not the
            // timezoneoffset of the evaluation request..... this is a bug waiting to happen
            if (value.start != null) {
                period.setStart(
                    toFhirDate(value.start as org.opencds.cqf.cql.engine.runtime.Date?)!!.getValue()
                )
            }

            if (value.end != null) {
                period.setEnd(
                    toFhirDate(value.end as org.opencds.cqf.cql.engine.runtime.Date?)!!.getValue()
                )
            }

            return period
        }

        throw IllegalArgumentException(
            "FHIR Period can only be created from an Interval of Date or DateTime type"
        )
    }

    override fun toFhirRange(value: Interval?): ICompositeType? {
        if (value == null) {
            return null
        }

        require(getSimpleName(value.pointType.getTypeName()) == "Quantity") {
            "FHIR Range can only be created from an Interval of Quantity type"
        }

        val range = Range()
        val start = toFhirQuantity(value.start as Quantity?) as org.hl7.fhir.dstu2.model.Quantity?
        if (start != null) {
            range.setLow(toSimpleQuantity(start))
        }

        val end = toFhirQuantity(value.end as Quantity?) as org.hl7.fhir.dstu2.model.Quantity?
        if (end != null) {
            range.setHigh(toSimpleQuantity(end))
        }

        return range
    }

    private fun addElementToParameter(
        param: Parameters.ParametersParameterComponent,
        key: String?,
        value: Any?,
    ) {
        if (value == null) {
            // Null value, add a single empty value with an extension indicating the reason
            val dataAbsentValue: BooleanType =
                emptyBooleanWithExtension(
                    DATA_ABSENT_REASON_EXT_URL,
                    CodeType(DATA_ABSENT_REASON_UNKNOWN_CODE),
                )
            addPartWithNameAndValue(param, key, dataAbsentValue)
            return
        }

        val iterable: Iterable<*>? = asIterable(value)
        if (iterable == null) {
            // Single, non-null value
            addPartWithNameAndValue(param, key, toFhirType(value)!!)
            return
        }

        if (!iterable.iterator().hasNext()) {
            // Empty list
            val emptyListValue: BooleanType =
                emptyBooleanWithExtension(EMPTY_LIST_EXT_URL, BooleanType(true))
            addPartWithNameAndValue(param, key, emptyListValue)
        } else {
            // Non-empty list, one part per value
            val fhirTypes = this.toFhirTypes(iterable)
            for (fhirType in fhirTypes) {
                addPartWithNameAndValue(param, key, fhirType!!)
            }
        }
    }

    override fun toFhirTuple(value: Tuple?): IBase? {
        if (value == null) {
            return null
        }

        val parameters = Parameters()
        val param = parameters.addParameter()

        if (value.getElements().isEmpty()) {
            param.setValue(emptyBooleanWithExtension(EMPTY_TUPLE_EXT_URL, BooleanType(true)))
        }

        for (key in value.getElements().keys) {
            addElementToParameter(param, key, value.getElements()[key])
        }

        return param
    }

    override fun toCqlQuantity(value: ICompositeType?): Quantity? {
        if (value == null) {
            return null
        }

        require(value.fhirType() == "Quantity") { "value is not a FHIR Quantity" }

        val quantity = value as org.hl7.fhir.dstu2.model.Quantity
        require(!quantity.hasComparator()) {
            "Cannot convert a FHIR Quantity with a comparator to a CQL quantity"
        }
        return Quantity()
            .withUnit(toCqlCalendarUnit(quantity.getUnit()))
            .withValue(quantity.getValue())
    }

    override fun toCqlRatio(value: ICompositeType?): Ratio? {
        if (value == null) {
            return null
        }

        require(value.fhirType() == "Ratio") { "value is not a FHIR Ratio" }

        val ratio = value as org.hl7.fhir.dstu2.model.Ratio

        return Ratio()
            .setNumerator(toCqlQuantity(ratio.getNumerator()))
            .setDenominator(toCqlQuantity(ratio.getDenominator()))
    }

    override fun toCqlAny(value: IBase?): Any? {
        if (value == null) {
            return null
        }

        throw NotImplementedException("Unable to convert to System.Any type")
    }

    override fun toCqlCode(value: IBaseCoding?): Code? {
        if (value == null) {
            return null
        }

        val coding = value as Coding

        return Code()
            .withSystem(coding.getSystem())
            .withCode(coding.getCode())
            .withVersion(coding.getVersion())
            .withDisplay(coding.getDisplay())
    }

    override fun toCqlConcept(value: ICompositeType?): Concept? {
        if (value == null) {
            return null
        }

        require(value.fhirType() == "CodeableConcept") { "value is not a FHIR CodeableConcept" }

        val codeableConcept = value as CodeableConcept

        return Concept()
            .withDisplay(codeableConcept.getText())
            .withCodes(
                codeableConcept
                    .getCoding()
                    .stream()
                    .map<Code?> { x: Coding? -> toCqlCode(x) }
                    .collect(Collectors.toList())
            )
    }

    override fun toCqlInterval(value: ICompositeType?): Interval? {
        if (value == null) {
            return null
        }

        if (value.fhirType() == "Range") {
            val range = value as Range
            return Interval(
                toCqlQuantity(range.getLow()),
                true,
                toCqlQuantity(range.getHigh()),
                true,
            )
        } else if (value.fhirType() == "Period") {
            val period = value as Period
            return Interval(
                toCqlTemporal(period.startElement),
                true,
                toCqlTemporal(period.endElement),
                true,
            )
        } else {
            throw IllegalArgumentException("value is not a FHIR Range or Period")
        }
    }

    override fun toCqlDate(value: IPrimitiveType<Date>?): org.opencds.cqf.cql.engine.runtime.Date? {
        if (value == null) {
            return null
        }

        require(!(value.fhirType() != "date" || value.fhirType() == "dateTime")) {
            "value is not a FHIR Date or DateTime"
        }

        val baseDateTime = value as BaseDateTimeType
        when (baseDateTime.precision) {
            TemporalPrecisionEnum.YEAR,
            TemporalPrecisionEnum.DAY,
            TemporalPrecisionEnum.MONTH ->
                return toDate(toCalendar(baseDateTime)!!, baseDateTime.precision.calendarConstant)

            TemporalPrecisionEnum.SECOND,
            TemporalPrecisionEnum.MILLI,
            TemporalPrecisionEnum.MINUTE ->
                throw IllegalArgumentException("value has a precision higher than a CQL Date")

            else -> throw IllegalArgumentException("value has a precision higher than a CQL Date")
        }
    }

    override fun toCqlDateTime(value: IPrimitiveType<Date>?): DateTime? {
        if (value == null) {
            return null
        }

        if (value.fhirType() == "instant" || value.fhirType() == "dateTime") {
            val baseDateTime = value as BaseDateTimeType
            return toDateTime(toCalendar(baseDateTime)!!, baseDateTime.precision.calendarConstant)
        } else {
            throw IllegalArgumentException("value is not a FHIR Instant or DateTime")
        }
    }

    override fun toCqlTemporal(value: IPrimitiveType<Date>?): BaseTemporal? {
        if (value == null) {
            return null
        }

        if (
            value.fhirType() == "instant" ||
                value.fhirType() == "dateTime" ||
                value.fhirType() == "date"
        ) {
            val baseDateTime = value as BaseDateTimeType
            when (baseDateTime.precision) {
                TemporalPrecisionEnum.YEAR,
                TemporalPrecisionEnum.DAY,
                TemporalPrecisionEnum.MONTH ->
                    return toDate(
                        toCalendar(baseDateTime)!!,
                        baseDateTime.precision.calendarConstant,
                    )

                TemporalPrecisionEnum.SECOND,
                TemporalPrecisionEnum.MILLI,
                TemporalPrecisionEnum.MINUTE ->
                    return toDateTime(
                        toCalendar(baseDateTime)!!,
                        baseDateTime.precision.calendarConstant,
                    )

                else ->
                    return toDateTime(
                        toCalendar(baseDateTime)!!,
                        baseDateTime.precision.calendarConstant,
                    )
            }
        } else {
            throw IllegalArgumentException("value is not a FHIR Instant or DateTime")
        }
    }

    override fun toFhirOperationOutcome(exception: Exception?): IBaseOperationOutcome? {
        if (exception == null) {
            return null
        }

        val outcome = OperationOutcome()
        outcome
            .addIssue()
            .setSeverity(OperationOutcome.IssueSeverity.ERROR)
            .setCode(OperationOutcome.IssueType.EXCEPTION)
            .setDiagnostics(exception.message)
            .addExtension(NATIVE_STACK_TRACE_EXT_URL, StringType(getStackTraceAsString(exception)))

        return outcome
    }

    override fun toCqlText(value: Any?): IBaseDatatype? {
        if (value == null) {
            return null
        }

        val s = ToStringEvaluator.toString(value) as String?
        val text = StringType(s)
        text.addExtension(CQL_TEXT_EXT_URL, BooleanType(true))
        return text
    }

    // The built-in "toCalendar" function is bugged.
    // It does not correctly populate the Calendar
    private fun toCalendar(dateTimeType: BaseDateTimeType): Calendar? {
        if (dateTimeType.value == null) {
            return null
        }
        val cal =
            if (dateTimeType.timeZone != null) {
                GregorianCalendar(dateTimeType.timeZone)
            } else {
                GregorianCalendar()
            }
        cal.setTime(dateTimeType.value)

        return cal
    }

    // The built-in "castToSimpleQuantity" automatically creates
    // missing elements, which causes comparisons to fail
    private fun toSimpleQuantity(quantity: org.hl7.fhir.dstu2.model.Quantity): SimpleQuantity {
        val simple = SimpleQuantity()
        simple.setValue(quantity.getValue())
        simple.setCode(quantity.getCode())
        simple.setSystem(quantity.getSystem())
        simple.setComparator(quantity.getComparator())
        return simple
    }

    companion object {
        private fun emptyBooleanWithExtension(url: String?, value: Type?): BooleanType {
            val result = BooleanType(null as String?)
            result.addExtension().setUrl(url).setValue(value)
            return result
        }

        private fun addPartWithNameAndValue(
            param: Parameters.ParametersParameterComponent,
            key: String?,
            value: Any,
        ) {
            if (value is Parameters.ParametersParameterComponent) {
                value.setName(key)
                param.addPart(value)
            } else {
                val part = param.addPart().setName(key)
                when (value) {
                    is Resource -> {
                        part.setResource(value)
                    }

                    is Type -> {
                        part.setValue(value)
                    }

                    else -> {
                        throw IllegalArgumentException(
                            "Unsupported FHIR type: " + value.javaClass.getName()
                        )
                    }
                }
            }
        }

        private fun asIterable(value: Any?): Iterable<*>? {
            return value as? Iterable<*>
        }
    }
}
