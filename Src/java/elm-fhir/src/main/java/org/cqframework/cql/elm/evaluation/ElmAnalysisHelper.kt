package org.cqframework.cql.elm.evaluation

import java.lang.String
import java.math.BigDecimal
import kotlin.IllegalArgumentException
import kotlin.Int
import kotlin.math.abs
import kotlin.text.StringBuilder
import kotlin.text.equals
import kotlin.text.format
import kotlin.toString
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.requirements.ElmRequirementsContext
import org.hl7.cql.model.IntervalType
import org.hl7.elm.r1.Date
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.End
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.Start
import org.hl7.elm.r1.Time
import org.hl7.fhir.r5.model.BooleanType
import org.hl7.fhir.r5.model.CodeableConcept
import org.hl7.fhir.r5.model.Coding
import org.hl7.fhir.r5.model.DataType
import org.hl7.fhir.r5.model.DateTimeType
import org.hl7.fhir.r5.model.DateType
import org.hl7.fhir.r5.model.DecimalType
import org.hl7.fhir.r5.model.Extension
import org.hl7.fhir.r5.model.IntegerType
import org.hl7.fhir.r5.model.Period
import org.hl7.fhir.r5.model.Quantity
import org.hl7.fhir.r5.model.Range
import org.hl7.fhir.r5.model.StringType
import org.hl7.fhir.r5.model.TimeType

object ElmAnalysisHelper {
    private fun toFhirDateTimeValue(
        context: ElmRequirementsContext,
        value: Expression?,
    ): DateTimeType? {
        if (value == null) {
            return null
        }

        val result = toFhirValue(context, value)
        if (result is DateTimeType) {
            return result
        }
        if (result is DateType) {
            return DateTimeType(result.valueAsString)
        }

        throw IllegalArgumentException("Could not convert expression to a DateTime value")
    }

    @JvmStatic
    fun toFhirValue(context: ElmRequirementsContext, value: Expression?): DataType? {
        if (value == null) {
            return null
        }

        // In the special case that the value is directly a parameter ref, use the parameter
        // extension mechanism
        if (value is ParameterRef) {
            val valueResultType = value.resultType
            if (context.typeResolver.isIntervalType(valueResultType)) {
                val e = toExpression(context, value)
                val pointType = (valueResultType as IntervalType).pointType
                if (
                    context.typeResolver.isDateTimeType(pointType) ||
                        context.typeResolver.isDateType(pointType)
                ) {
                    val period = Period()
                    period.addExtension(e)
                    return period
                } else if (
                    context.typeResolver.isQuantityType(pointType) ||
                        context.typeResolver.isIntegerType(pointType) ||
                        context.typeResolver.isDecimalType(pointType)
                ) {
                    val range = Range()
                    range.addExtension(e)
                    return range
                } else {
                    throw IllegalArgumentException(
                        String.format(
                            "toFhirValue not implemented for interval of %s",
                            pointType.toString(),
                        )
                    )
                }
            } else if (context.typeResolver.isBooleanType(valueResultType)) {
                val result = BooleanType()
                result.addExtension(toExpression(context, value))
                return result
            } else if (context.typeResolver.isIntegerType(valueResultType)) {
                val result = IntegerType()
                result.addExtension(toExpression(context, value))
                return result
            } else if (context.typeResolver.isDecimalType(valueResultType)) {
                val result = DecimalType()
                result.addExtension(toExpression(context, value))
                return result
            } else if (context.typeResolver.isQuantityType(valueResultType)) {
                val result = Quantity()
                result.addExtension(toExpression(context, value))
                return result
            } else if (context.typeResolver.isCodeType(valueResultType)) {
                val result = Coding()
                result.addExtension(toExpression(context, value))
                return result
            } else if (context.typeResolver.isConceptType(valueResultType)) {
                val result = CodeableConcept()
                result.addExtension(toExpression(context, value))
                return result
            } else if (context.typeResolver.isDateType(valueResultType)) {
                val result = DateType()
                result.addExtension(toExpression(context, value))
                return result
            } else if (context.typeResolver.isDateTimeType(valueResultType)) {
                val result = DateTimeType()
                result.addExtension(toExpression(context, value))
                return result
            } else if (context.typeResolver.isTimeType(valueResultType)) {
                val result = TimeType()
                result.addExtension(toExpression(context, value))
                return result
            } else {
                throw IllegalArgumentException(
                    String.format(
                        "toFhirValue not implemented for parameter of type %s",
                        valueResultType.toString(),
                    )
                )
            }
        }

        // Attempt to convert the CQL value to a FHIR value:
        if (value is Interval) {
            // TODO: Handle lowclosed/highclosed
            return Period()
                .setStartElement(toFhirDateTimeValue(context, value.low))
                .setEndElement(toFhirDateTimeValue(context, value.high))
        } else if (value is Literal) {
            val valueResultType = value.resultType
            if (context.typeResolver.isDateTimeType(valueResultType)) {
                return DateTimeType(value.value)
            } else if (context.typeResolver.isDateType(valueResultType)) {
                return DateType(value.value)
            } else if (context.typeResolver.isIntegerType(valueResultType)) {
                return IntegerType(value.value)
            } else if (context.typeResolver.isDecimalType(valueResultType)) {
                return DecimalType(value.value)
            } else if (context.typeResolver.isStringType(valueResultType)) {
                return StringType(value.value)
            }
        } else if (value is DateTime) {
            return DateTimeType(
                toDateTimeString(
                    toFhirValue(context, value.year),
                    toFhirValue(context, value.month),
                    toFhirValue(context, value.day),
                    toFhirValue(context, value.hour),
                    toFhirValue(context, value.minute),
                    toFhirValue(context, value.second),
                    toFhirValue(context, value.millisecond),
                    toFhirValue(context, value.timezoneOffset),
                )
            )
        } else if (value is Date) {
            return DateType(
                toDateString(
                    toFhirValue(context, value.year),
                    toFhirValue(context, value.month),
                    toFhirValue(context, value.day),
                )
            )
        } else if (value is Time) {
            return TimeType(
                toTimeString(
                    toFhirValue(context, value.hour),
                    toFhirValue(context, value.minute),
                    toFhirValue(context, value.second),
                    toFhirValue(context, value.millisecond),
                )
            )
        } else if (value is Start) {
            val operand = toFhirValue(context, value.operand)
            if (operand != null) {
                val period = operand as Period
                return period.startElement
            }
        } else if (value is End) {
            val operand = toFhirValue(context, value.operand)
            if (operand != null) {
                val period = operand as Period
                return period.endElement
            }
        }

        throw IllegalArgumentException(
            "toFhirValue not implemented for ${value.javaClass.simpleName}"
        )
    }

    // Can't believe I have to write this, there seriously isn't a String.format option for this!!!!
    private fun padLeft(
        input: kotlin.String?,
        width: Int,
        padWith: kotlin.String?,
    ): kotlin.String? {
        var input = input
        if (input == null || padWith == null || padWith.length == 0) {
            return null
        }

        // Can't believe I have to do this, why is repeat not available until Java 11!!!!!
        while (input!!.length < width) {
            input = padWith + input
        }

        return input
    }

    private fun padZero(input: kotlin.String?, width: Int): kotlin.String? {
        return padLeft(input, width, "0")
    }

    // Ugly to have to do this here, but cannot reuse engine evaluation logic without a major
    // refactor
    // TODO: Consider refactoring to reuse engine evaluation logic here
    private fun toDateTimeString(
        year: DataType?,
        month: DataType?,
        day: DataType?,
        hour: DataType?,
        minute: DataType?,
        second: DataType?,
        millisecond: DataType?,
        timezoneOffset: DataType?,
    ): kotlin.String? {
        if (year == null) {
            return null
        }

        val result = StringBuilder()
        if (year is IntegerType) {
            result.append(padZero(year.value.toString(), 4))
        }
        if (month is IntegerType) {
            result.append("-")
            result.append(padZero(month.value.toString(), 2))
        }
        if (day is IntegerType) {
            result.append("-")
            result.append(padZero(day.value.toString(), 2))
        }
        if (hour is IntegerType) {
            result.append("T")
            result.append(padZero(hour.value.toString(), 2))
        }
        if (minute is IntegerType) {
            result.append(":")
            result.append(padZero(minute.value.toString(), 2))
        }
        if (second is IntegerType) {
            result.append(":")
            result.append(padZero(second.value.toString(), 2))
        }
        if (millisecond is IntegerType) {
            result.append(".")
            result.append(padZero(millisecond.value.toString(), 3))
        }
        if (timezoneOffset is DecimalType) {
            val offset = timezoneOffset.value
            if (offset.toInt() >= 0) {
                result.append("+")
                result.append(padZero(offset.toInt().toString(), 2))
            } else {
                result.append("-")
                result.append(padZero(abs(offset.toInt()).toString(), 2))
            }
            val minutes = BigDecimal("60").multiply(offset.remainder(BigDecimal.ONE)).toInt()
            result.append(":")
            result.append(padZero(minutes.toString(), 2))
        }

        return result.toString()
    }

    private fun toDateString(year: DataType?, month: DataType?, day: DataType?): kotlin.String? {
        if (year == null) {
            return null
        }

        val result = StringBuilder()
        if (year is IntegerType) {
            result.append(padZero(year.value.toString(), 4))
        }
        if (month is IntegerType) {
            result.append("-")
            result.append(padZero(month.value.toString(), 2))
        }
        if (day is IntegerType) {
            result.append("-")
            result.append(padZero(day.value.toString(), 2))
        }

        return result.toString()
    }

    private fun toTimeString(
        hour: DataType?,
        minute: DataType?,
        second: DataType?,
        millisecond: DataType?,
    ): kotlin.String? {
        if (hour == null) {
            return null
        }

        val result = StringBuilder()
        if (hour is IntegerType) {
            result.append(padZero(hour.value.toString(), 2))
        }
        if (minute is IntegerType) {
            result.append(":")
            result.append(padZero(minute.value.toString(), 2))
        }
        if (second is IntegerType) {
            result.append(":")
            result.append(padZero(second.value.toString(), 2))
        }
        if (millisecond is IntegerType) {
            result.append(".")
            result.append(padZero(millisecond.value.toString(), 3))
        }

        return result.toString()
    }

    private fun toExpression(
        context: ElmRequirementsContext,
        parameterRef: ParameterRef,
    ): Extension? {
        var expression = parameterRef.name
        if (
            parameterRef.libraryName != null &&
                !parameterRef.libraryName.equals(context.getCurrentLibraryIdentifier().id)
        ) {
            expression =
                kotlin.String.format("\"%s\".\"%s\"", parameterRef.libraryName, parameterRef.name)
        }
        return Extension()
            .setUrl("http://hl7.org/fhir/StructureDefinition/cqf-expression")
            .setValue(
                org.hl7.fhir.r5.model
                    .Expression()
                    .setLanguage("text/cql-identifier")
                    .setExpression(expression)
            )
    }
}
