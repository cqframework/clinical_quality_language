package org.cqframework.cql.elm.evaluation;

import java.math.BigDecimal;
import org.cqframework.cql.elm.requirements.ElmRequirementsContext;
import org.hl7.cql.model.IntervalType;
import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Expression;
import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.r5.model.Quantity;

public class ElmAnalysisHelper {
    private static DateTimeType toFhirDateTimeValue(ElmRequirementsContext context, Expression value) {
        if (value == null) {
            return null;
        }

        DataType result = toFhirValue(context, value);
        if (result instanceof DateTimeType) {
            return (DateTimeType) result;
        }
        if (result instanceof DateType) {
            return new DateTimeType(((DateType) result).getValueAsString());
        }

        throw new IllegalArgumentException("Could not convert expression to a DateTime value");
    }

    public static DataType toFhirValue(ElmRequirementsContext context, Expression value) {
        if (value == null) {
            return null;
        }

        // In the special case that the value is directly a parameter ref, use the parameter extension mechanism
        if (value instanceof ParameterRef) {
            if (context.getTypeResolver().isIntervalType(value.getResultType())) {
                Extension e = toExpression(context, (ParameterRef) value);
                org.hl7.cql.model.DataType pointType = ((IntervalType) value.getResultType()).getPointType();
                if (context.getTypeResolver().isDateTimeType(pointType)
                        || context.getTypeResolver().isDateType(pointType)) {
                    Period period = new Period();
                    period.addExtension(e);
                    return period;
                } else if (context.getTypeResolver().isQuantityType(pointType)
                        || context.getTypeResolver().isIntegerType(pointType)
                        || context.getTypeResolver().isDecimalType(pointType)) {
                    Range range = new Range();
                    range.addExtension(e);
                    return range;
                } else {
                    throw new IllegalArgumentException(
                            String.format("toFhirValue not implemented for interval of %s", pointType.toString()));
                }
            }
            // Boolean, Integer, Decimal, String, Quantity, Date, DateTime, Time, Coding, CodeableConcept
            else if (context.getTypeResolver().isBooleanType(value.getResultType())) {
                BooleanType result = new BooleanType();
                result.addExtension(toExpression(context, (ParameterRef) value));
                return result;
            } else if (context.getTypeResolver().isIntegerType(value.getResultType())) {
                IntegerType result = new IntegerType();
                result.addExtension(toExpression(context, (ParameterRef) value));
                return result;
            } else if (context.getTypeResolver().isDecimalType(value.getResultType())) {
                DecimalType result = new DecimalType();
                result.addExtension(toExpression(context, (ParameterRef) value));
                return result;
            } else if (context.getTypeResolver().isQuantityType(value.getResultType())) {
                Quantity result = new Quantity();
                result.addExtension(toExpression(context, (ParameterRef) value));
                return result;
            } else if (context.getTypeResolver().isCodeType(value.getResultType())) {
                Coding result = new Coding();
                result.addExtension(toExpression(context, (ParameterRef) value));
                return result;

            } else if (context.getTypeResolver().isConceptType(value.getResultType())) {
                CodeableConcept result = new CodeableConcept();
                result.addExtension(toExpression(context, (ParameterRef) value));
                return result;
            } else if (context.getTypeResolver().isDateType(value.getResultType())) {
                DateType result = new DateType();
                result.addExtension(toExpression(context, (ParameterRef) value));
                return result;
            } else if (context.getTypeResolver().isDateTimeType(value.getResultType())) {
                DateTimeType result = new DateTimeType();
                result.addExtension(toExpression(context, (ParameterRef) value));
                return result;
            } else if (context.getTypeResolver().isTimeType(value.getResultType())) {
                TimeType result = new TimeType();
                result.addExtension(toExpression(context, (ParameterRef) value));
                return result;
            } else {
                throw new IllegalArgumentException(String.format(
                        "toFhirValue not implemented for parameter of type %s",
                        value.getResultType().toString()));
            }
        }

        // Attempt to convert the CQL value to a FHIR value:
        if (value instanceof Interval) {
            // TODO: Handle lowclosed/highclosed
            return new Period()
                    .setStartElement(toFhirDateTimeValue(context, ((Interval) value).getLow()))
                    .setEndElement(toFhirDateTimeValue(context, ((Interval) value).getHigh()));
        } else if (value instanceof Literal) {
            if (context.getTypeResolver().isDateTimeType(value.getResultType())) {
                return new DateTimeType(((Literal) value).getValue());
            } else if (context.getTypeResolver().isDateType(value.getResultType())) {
                return new DateType(((Literal) value).getValue());
            } else if (context.getTypeResolver().isIntegerType(value.getResultType())) {
                return new IntegerType(((Literal) value).getValue());
            } else if (context.getTypeResolver().isDecimalType(value.getResultType())) {
                return new DecimalType(((Literal) value).getValue());
            } else if (context.getTypeResolver().isStringType(value.getResultType())) {
                return new StringType(((Literal) value).getValue());
            }
        } else if (value instanceof DateTime) {
            DateTime dateTime = (DateTime) value;
            return new DateTimeType(toDateTimeString(
                    toFhirValue(context, dateTime.getYear()),
                    toFhirValue(context, dateTime.getMonth()),
                    toFhirValue(context, dateTime.getDay()),
                    toFhirValue(context, dateTime.getHour()),
                    toFhirValue(context, dateTime.getMinute()),
                    toFhirValue(context, dateTime.getSecond()),
                    toFhirValue(context, dateTime.getMillisecond()),
                    toFhirValue(context, dateTime.getTimezoneOffset())));
        } else if (value instanceof org.hl7.elm.r1.Date) {
            org.hl7.elm.r1.Date date = (org.hl7.elm.r1.Date) value;
            return new DateType(toDateString(
                    toFhirValue(context, date.getYear()),
                    toFhirValue(context, date.getMonth()),
                    toFhirValue(context, date.getDay())));
        } else if (value instanceof Time) {
            Time time = (Time) value;
            return new TimeType(toTimeString(
                    toFhirValue(context, time.getHour()),
                    toFhirValue(context, time.getMinute()),
                    toFhirValue(context, time.getSecond()),
                    toFhirValue(context, time.getMillisecond())));
        } else if (value instanceof Start) {
            DataType operand = toFhirValue(context, ((Start) value).getOperand());
            if (operand != null) {
                Period period = (Period) operand;
                return period.getStartElement();
            }
        } else if (value instanceof End) {
            DataType operand = toFhirValue(context, ((End) value).getOperand());
            if (operand != null) {
                Period period = (Period) operand;
                return period.getEndElement();
            }
        }

        throw new IllegalArgumentException(String.format(
                "toFhirValue not implemented for %s", value.getClass().getSimpleName()));
    }

    // Can't believe I have to write this, there seriously isn't a String.format option for this!!!!
    private static String padLeft(String input, int width, String padWith) {
        if (input == null || padWith == null || padWith.length() == 0) {
            return null;
        }

        // Can't believe I have to do this, why is repeat not available until Java 11!!!!!
        while (input.length() < width) {
            input = padWith + input;
        }

        return input;
    }

    private static String padZero(String input, int width) {
        return padLeft(input, width, "0");
    }

    // Ugly to have to do this here, but cannot reuse engine evaluation logic without a major refactor
    // TODO: Consider refactoring to reuse engine evaluation logic here
    private static String toDateTimeString(
            DataType year,
            DataType month,
            DataType day,
            DataType hour,
            DataType minute,
            DataType second,
            DataType millisecond,
            DataType timezoneOffset) {
        if (year == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        if (year instanceof IntegerType) {
            result.append(padZero(((IntegerType) year).getValue().toString(), 4));
        }
        if (month instanceof IntegerType) {
            result.append("-");
            result.append(padZero(((IntegerType) month).getValue().toString(), 2));
        }
        if (day instanceof IntegerType) {
            result.append("-");
            result.append(padZero(((IntegerType) day).getValue().toString(), 2));
        }
        if (hour instanceof IntegerType) {
            result.append("T");
            result.append(padZero(((IntegerType) hour).getValue().toString(), 2));
        }
        if (minute instanceof IntegerType) {
            result.append(":");
            result.append(padZero(((IntegerType) minute).getValue().toString(), 2));
        }
        if (second instanceof IntegerType) {
            result.append(":");
            result.append(padZero(((IntegerType) second).getValue().toString(), 2));
        }
        if (millisecond instanceof IntegerType) {
            result.append(".");
            result.append(padZero(((IntegerType) millisecond).getValue().toString(), 3));
        }
        if (timezoneOffset instanceof DecimalType) {
            BigDecimal offset = ((DecimalType) timezoneOffset).getValue();
            if (offset.intValue() >= 0) {
                result.append("+");
                result.append(padZero(Integer.toString(offset.intValue()), 2));
            } else {
                result.append("-");
                result.append(padZero(Integer.toString(Math.abs(offset.intValue())), 2));
            }
            int minutes = new BigDecimal("60")
                    .multiply(offset.remainder(BigDecimal.ONE))
                    .intValue();
            result.append(":");
            result.append(padZero(Integer.toString(minutes), 2));
        }

        return result.toString();
    }

    private static String toDateString(DataType year, DataType month, DataType day) {
        if (year == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        if (year instanceof IntegerType) {
            result.append(padZero(((IntegerType) year).getValue().toString(), 4));
        }
        if (month instanceof IntegerType) {
            result.append("-");
            result.append(padZero(((IntegerType) month).getValue().toString(), 2));
        }
        if (day instanceof IntegerType) {
            result.append("-");
            result.append(padZero(((IntegerType) day).getValue().toString(), 2));
        }

        return result.toString();
    }

    private static String toTimeString(DataType hour, DataType minute, DataType second, DataType millisecond) {
        if (hour == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        if (hour instanceof IntegerType) {
            result.append(padZero(((IntegerType) hour).getValue().toString(), 2));
        }
        if (minute instanceof IntegerType) {
            result.append(":");
            result.append(padZero(((IntegerType) minute).getValue().toString(), 2));
        }
        if (second instanceof IntegerType) {
            result.append(":");
            result.append(padZero(((IntegerType) second).getValue().toString(), 2));
        }
        if (millisecond instanceof IntegerType) {
            result.append(".");
            result.append(padZero(((IntegerType) millisecond).getValue().toString(), 3));
        }

        return result.toString();
    }

    private static Extension toExpression(ElmRequirementsContext context, ParameterRef parameterRef) {
        String expression = parameterRef.getName();
        if (parameterRef.getLibraryName() != null
                && !parameterRef
                        .getLibraryName()
                        .equals(context.getCurrentLibraryIdentifier().getId())) {
            expression = String.format("\"%s\".\"%s\"", parameterRef.getLibraryName(), parameterRef.getName());
        }
        return new Extension()
                .setUrl("http://hl7.org/fhir/StructureDefinition/cqf-expression")
                .setValue(new org.hl7.fhir.r5.model.Expression()
                        .setLanguage("text/cql-identifier")
                        .setExpression(expression));
    }
}
