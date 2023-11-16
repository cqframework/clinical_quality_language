package org.opencds.cqf.cql.engine.fhir.converter;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu2.model.*;
import org.opencds.cqf.cql.engine.runtime.*;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Ratio;

import org.apache.commons.lang3.NotImplementedException;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;

class Dstu2FhirTypeConverter extends BaseFhirTypeConverter {

    @Override
    public IIdType toFhirId(String value) {
        if (value == null) {
            return null;
        }

        return new IdType(value);
    }

    @Override
    public BooleanType toFhirBoolean(Boolean value) {
        if (value == null) {
            return null;
        }

        return new BooleanType(value);
    }

    @Override
    public IPrimitiveType<Integer> toFhirInteger(Integer value) {
        if (value == null) {
            return null;
        }

        return new IntegerType(value);
    }

    @Override
    public IPrimitiveType<BigDecimal> toFhirDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }

        return new DecimalType(value);
    }

    @Override
    public IPrimitiveType<java.util.Date> toFhirDate(Date value) {
        if (value == null) {
            return null;
        }

        return new DateType(value.toString());
    }

    @Override
    public IPrimitiveType<java.util.Date> toFhirDateTime(DateTime value) {
        if (value == null) {
            return null;
        }

        final DateTimeType result = new DateTimeType(value.toDateString());
        result.setPrecision(toFhirPrecision(value.getPrecision()));
        return result;
    }

    @Override
    public IPrimitiveType<String> toFhirTime(Time value) {
        if (value == null) {
            return null;
        }

        return new TimeType(value.toString());
    }

    @Override
    public IPrimitiveType<String> toFhirString(String value) {
        if (value == null) {
            return null;
        }

        return new StringType(value);
    }

    @Override
    public ICompositeType toFhirQuantity(Quantity value) {
        if (value == null) {
            return null;
        }

        String unit = value.getUnit();
        String system = isCqlCalendarUnit(unit) ? "http://hl7.org/fhirpath/CodeSystem/calendar-units" : "http://unitsofmeasure.org";
        String ucumUnit = toUcumUnit(unit);

        return new org.hl7.fhir.dstu2.model.Quantity()
                .setSystem(system).setCode(ucumUnit).setValue(value.getValue()).setUnit(unit);
    }

    @Override
    public ICompositeType toFhirRatio(Ratio value) {
        if (value == null) {
            return null;
        }

        return new org.hl7.fhir.dstu2.model.Ratio()
                .setNumerator((org.hl7.fhir.dstu2.model.Quantity) toFhirQuantity(value.getNumerator()))
                .setDenominator((org.hl7.fhir.dstu2.model.Quantity) toFhirQuantity(value.getDenominator()));
    }

    @Override
    public IBase toFhirAny(Object value) {
        if (value == null) {
            return null;
        }

        throw new NotImplementedException("Unable to convert System.Any types");
    }

    @Override
    public IBaseCoding toFhirCoding(Code value) {
        if (value == null) {
            return null;
        }

        Coding coding = new Coding();
        coding.setSystem(value.getSystem());
        coding.setCode(value.getCode());
        coding.setDisplay(value.getDisplay());
        coding.setVersion(value.getVersion());
        return coding;
    }

    @Override
    public ICompositeType toFhirCodeableConcept(Concept value) {
        if (value == null) {
            return null;
        }

        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setText(value.getDisplay());
        if (value.getCodes() != null) {
            for (Code c : value.getCodes()) {
                codeableConcept.addCoding((Coding) toFhirCoding(c));
            }
        }

        return codeableConcept;
    }

    @Override
    public ICompositeType toFhirPeriod(Interval value) {
        if (value == null) {
            return null;
        }

        Period period = new Period();
        if (getSimpleName(value.getPointType().getTypeName()).equals("DateTime")) {
            if (value.getStart() != null) {
                period.setStartElement((DateTimeType)toFhirDateTime((DateTime)value.getStart()));
            }

            if (value.getEnd() != null) {
                period.setEndElement((DateTimeType)toFhirDateTime((DateTime)value.getEnd()));
            }

            return period;
        } else if (getSimpleName(value.getPointType().getTypeName()).equals("Date")) {
            // TODO: This will construct DateTimeType values in FHIR with the system timezone id, not the
            // timezoneoffset of the evaluation request..... this is a bug waiting to happen
            if (value.getStart() != null) {
                period.setStart(toFhirDate((Date) value.getStart()).getValue());
            }

            if (value.getEnd() != null) {
                period.setEnd(toFhirDate((Date) value.getEnd()).getValue());
            }

            return period;
        }

        throw new IllegalArgumentException("FHIR Period can only be created from an Interval of Date or DateTime type");
    }

    @Override
    public ICompositeType toFhirRange(Interval value) {
        if (value == null) {
            return null;
        }

        if (!getSimpleName(value.getPointType().getTypeName()).equals("Quantity")) {
            throw new IllegalArgumentException("FHIR Range can only be created from an Interval of Quantity type");
        }

        Range range = new Range();
        org.hl7.fhir.dstu2.model.Quantity start = (org.hl7.fhir.dstu2.model.Quantity)toFhirQuantity((Quantity)value.getStart());
        if (start != null) {
            range.setLow(toSimpleQuantity(start));
        }

        org.hl7.fhir.dstu2.model.Quantity end = (org.hl7.fhir.dstu2.model.Quantity)toFhirQuantity((Quantity)value.getEnd());
        if (end != null) {
            range.setHigh(toSimpleQuantity(end));
        }

        return range;
    }

    @Override
    public IBase toFhirTuple(Tuple value) {
        if (value == null) {
            return null;
        }

        throw new NotImplementedException("can't convert Tuples");
    }

    @Override
    public Quantity toCqlQuantity(ICompositeType value) {
        if (value == null) {
            return null;
        }

        if (!value.fhirType().equals("Quantity")) {
            throw new IllegalArgumentException("value is not a FHIR Quantity");
        }

        org.hl7.fhir.dstu2.model.Quantity quantity = (org.hl7.fhir.dstu2.model.Quantity) value;
        if (quantity.hasComparator()) {
            throw new IllegalArgumentException("Cannot convert a FHIR Quantity with a comparator to a CQL quantity");
        }
        return new Quantity().withUnit(toCqlCalendarUnit(quantity.getUnit())).withValue(quantity.getValue());
    }

    @Override
    public Ratio toCqlRatio(ICompositeType value) {
        if (value == null) {
            return null;
        }

        if (!value.fhirType().equals("Ratio")) {
            throw new IllegalArgumentException("value is not a FHIR Ratio");
        }

        org.hl7.fhir.dstu2.model.Ratio ratio = (org.hl7.fhir.dstu2.model.Ratio) value;

        return new Ratio().setNumerator(toCqlQuantity(ratio.getNumerator()))
                .setDenominator(toCqlQuantity(ratio.getDenominator()));
    }

    @Override
    public Object toCqlAny(IBase value) {
        if (value == null) {
            return null;
        }

        throw new NotImplementedException("Unable to convert to System.Any type");
    }

    @Override
    public Code toCqlCode(IBaseCoding value) {
        if (value == null) {
            return null;
        }

        Coding coding = (Coding) value;

        return new Code().withSystem(coding.getSystem()).withCode(coding.getCode()).withVersion(coding.getVersion())
                .withDisplay(coding.getDisplay());
    }

    @Override
    public Concept toCqlConcept(ICompositeType value) {
        if (value == null) {
            return null;
        }

        if (!value.fhirType().equals("CodeableConcept")) {
            throw new IllegalArgumentException("value is not a FHIR CodeableConcept");
        }

        CodeableConcept codeableConcept = (CodeableConcept) value;

        return new Concept().withDisplay(codeableConcept.getText())
                .withCodes(codeableConcept.getCoding().stream().map(x -> toCqlCode(x)).collect(Collectors.toList()));
    }

    @Override
    public Interval toCqlInterval(ICompositeType value) {
        if (value == null) {
            return null;
        }

        if (value.fhirType().equals("Range")) {
            Range range = (Range) value;
            return new Interval(toCqlQuantity(range.getLow()), true, toCqlQuantity(range.getHigh()), true);
        } else if (value.fhirType().equals("Period")) {
            Period period = (Period)value;
            return new Interval(toCqlTemporal(period.getStartElement()), true, toCqlTemporal(period.getEndElement()), true);
        } else {
            throw new IllegalArgumentException("value is not a FHIR Range or Period");
        }
    }

    @Override
    public Date toCqlDate(IPrimitiveType<java.util.Date> value) {
        if (value == null) {
            return null;
        }

        if (!value.fhirType().equals("date") || value.fhirType().equals("dateTime")) {
            throw new IllegalArgumentException("value is not a FHIR Date or DateTime");
        }

        BaseDateTimeType baseDateTime = (BaseDateTimeType) value;
        switch (baseDateTime.getPrecision()) {
            case YEAR:
            case DAY:
            case MONTH:
                return toDate(toCalendar(baseDateTime), baseDateTime.getPrecision().getCalendarConstant());
            case SECOND:
            case MILLI:
            case MINUTE:
            default:
                throw new IllegalArgumentException("value has a precision higher than a CQL Date");
        }
    }

    @Override
    public DateTime toCqlDateTime(IPrimitiveType<java.util.Date> value) {
        if (value == null) {
            return null;
        }

        if (value.fhirType().equals("instant") || value.fhirType().equals("dateTime")) {
            BaseDateTimeType baseDateTime = (BaseDateTimeType) value;
            return toDateTime(toCalendar(baseDateTime), baseDateTime.getPrecision().getCalendarConstant());
        } else {
            throw new IllegalArgumentException("value is not a FHIR Instant or DateTime");
        }
    }

    @Override
    public BaseTemporal toCqlTemporal(IPrimitiveType<java.util.Date> value) {
        if (value == null) {
            return null;
        }

        if (value.fhirType().equals("instant") || value.fhirType().equals("dateTime") || value.fhirType().equals("date")) {
            BaseDateTimeType baseDateTime = (BaseDateTimeType) value;
            switch (baseDateTime.getPrecision()) {
                case YEAR:
                case DAY:
                case MONTH:
                    return toDate(toCalendar(baseDateTime), baseDateTime.getPrecision().getCalendarConstant());
                case SECOND:
                case MILLI:
                case MINUTE:
                default:
                return toDateTime(toCalendar(baseDateTime), baseDateTime.getPrecision().getCalendarConstant());
            }
        } else {
            throw new IllegalArgumentException("value is not a FHIR Instant or DateTime");
        }
    }

    // The built-in "toCalendar" function is bugged.
    // It does not correctly populate the Calendar
    private Calendar toCalendar(BaseDateTimeType dateTimeType) {
        if (dateTimeType.getValue() == null) {
			return null;
		}
		GregorianCalendar cal;
		if (dateTimeType.getTimeZone() != null) {
			cal = new GregorianCalendar(dateTimeType.getTimeZone());
		} else {
			cal = new GregorianCalendar();
		}
        cal.setTime(dateTimeType.getValue());

        return cal;
    }

    // The built-in "castToSimpleQuantity" automatically creates
    // missing elements, which causes comparisons to fail
    private SimpleQuantity toSimpleQuantity(org.hl7.fhir.dstu2.model.Quantity quantity) {
        SimpleQuantity simple = new SimpleQuantity();
        simple.setValue(quantity.getValue());
        simple.setCode(quantity.getCode());
        simple.setSystem(quantity.getSystem());
        simple.setComparator(quantity.getComparator());
        return simple;
    }
}
