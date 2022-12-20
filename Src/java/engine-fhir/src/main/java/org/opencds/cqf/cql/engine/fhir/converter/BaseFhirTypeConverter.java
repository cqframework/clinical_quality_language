package org.opencds.cqf.cql.engine.fhir.converter;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import org.apache.commons.lang3.NotImplementedException;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.opencds.cqf.cql.engine.exception.InvalidPrecision;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.CqlType;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Ratio;
import org.opencds.cqf.cql.engine.runtime.TemporalHelper;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.opencds.cqf.cql.engine.runtime.Tuple;

abstract class BaseFhirTypeConverter implements FhirTypeConverter {

    @Override
    public boolean isFhirType(Object value) {
        Objects.requireNonNull(value, "value can not be null");

        if (value instanceof Iterable<?>) {
            throw new IllegalArgumentException("isFhirType can not be used for Iterables");
        }

        return value instanceof IBase;
    }

    @Override
    public Iterable<Object> toFhirTypes(Iterable<?> values) {
        List<Object> converted = new ArrayList<>();
        for (Object value : values) {
            if (value == null) {
                converted.add(null);
            }
            else if (value instanceof Iterable<?>) {
                converted.add(toFhirTypes((Iterable<?>)value));
            }
            else if (isFhirType(value)) {
                converted.add(value);
            }
            else if (isCqlType(value)) {
                converted.add(toFhirType(value));
            }
            else {
                throw new IllegalArgumentException(String.format("Unknown type encountered during conversion %s", value.getClass().getName()));
            }
        }

        return converted;
    }

    @Override
    public IBase toFhirType(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Iterable<?>) {
            throw new IllegalArgumentException("use toFhirTypes(Iterable<Object>) for iterables");
        }

        if (isFhirType(value)) {
            return (IBase)value;
        }

        if (!isCqlType(value)) {
            throw new IllegalArgumentException(
                    String.format("can't convert %s to FHIR type", value.getClass().getName()));
        }

        switch (value.getClass().getSimpleName()) {
            case "Boolean": return toFhirBoolean((Boolean) value);
            case "Integer": return toFhirInteger((Integer) value);
            case "BigDecimal": return toFhirDecimal((BigDecimal) value);
            case "Date": return toFhirDate((Date) value);
            case "DateTime": return toFhirDateTime((DateTime) value);
            case "Time": return toFhirTime((Time) value);
            case "String": return toFhirString((String) value);
            case "Quantity": return toFhirQuantity((Quantity) value);
            case "Ratio": return toFhirRatio((Ratio) value);
            case "Any": return toFhirAny(value);
            case "Code": return toFhirCoding((Code) value);
            case "Concept": return toFhirCodeableConcept((Concept) value);
            case "Interval": return toFhirInterval((Interval) value);
            case "Tuple": return toFhirTuple((Tuple) value);
            default:
                throw new IllegalArgumentException(
                        String.format("missing case statement for: %s", value.getClass().getName()));
        }
    }

    @Override
    public ICompositeType toFhirInterval(Interval value) {
        if (value == null) {
            return null;
        }

        switch (getSimpleName(value.getPointType().getTypeName())) {
            case "Date":
            case "DateTime":
                return toFhirPeriod(value);
            case "Quantity":
                return toFhirRange(value);
            default:
                throw new IllegalArgumentException(String.format(
                        "Unsupported interval point type for FHIR conversion %s", value.getPointType().getTypeName()));
        }
    }

    @Override
    public Boolean isCqlType(Object value) {
        Objects.requireNonNull(value, "value can not be null");

        if (value instanceof Iterable<?>) {
            throw new IllegalArgumentException("isCqlType can not be used for Iterables");
        }

        if (value instanceof CqlType) {
            return true;
        }

        if (value instanceof BigDecimal || value instanceof String || value instanceof Integer
                || value instanceof Boolean) {
            return true;
        }

        return false;
    }

    @Override
    public Iterable<Object> toCqlTypes(Iterable<?> values) {
        List<Object> converted = new ArrayList<>();
        for (Object value : values) {
            if (value == null) {
                converted.add(null);
            }
            else if (value instanceof Iterable<?>) {
                converted.add(toCqlTypes((Iterable<?>)value));
            }
            else if (isCqlType(value)) {
                converted.add(value);
            }
            else if (isFhirType(value)) {
                converted.add(toCqlType((IBase)value));
            }
            else {
                throw new IllegalArgumentException(String.format("Unknown type encountered during conversion %s", value.getClass().getName()));
            }
        }

        return converted;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object toCqlType(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Iterable<?>) {
            throw new IllegalArgumentException("use toCqlTypes(Iterable<Object>) for iterables");
        }

        if (isCqlType(value)) {
            return value;
        }

        if (!isFhirType(value)) {
            throw new IllegalArgumentException(
                    String.format("can't convert %s to CQL type", value.getClass().getName()));
        }

        switch (value.getClass().getSimpleName()) {
            // NOTE: There's no first class IdType in CQL, so the conversion to CQL Ids and back is asymmetric
            case "IdType": return toCqlId((IIdType)value);
            case "BooleanType": return toCqlBoolean((IPrimitiveType<Boolean>) value);
            case "IntegerType": return toCqlInteger((IPrimitiveType<Integer>) value);
            case "DecimalType": return toCqlDecimal((IPrimitiveType<BigDecimal>) value);
            case "DateType": return toCqlDate((IPrimitiveType<java.util.Date>) value);
            // NOTE: There's no first class InstantType in CQL, so the conversation to CQL DateTime and back is asymmetric
            case "InstantType":
            case "DateTimeType": return toCqlDateTime((IPrimitiveType<java.util.Date>) value);
            case "TimeType": return toCqlTime((IPrimitiveType<String>) value);
            case "StringType": return toCqlString((IPrimitiveType<String>)value);
            case "Quantity": return toCqlQuantity((ICompositeType) value);
            case "Ratio": return toCqlRatio((ICompositeType) value);
            case "Coding": return toCqlCode((IBaseCoding) value);
            case "CodeableConcept": return toCqlConcept((ICompositeType) value);
            case "Period":
            case "Range":
                    return toCqlInterval((ICompositeType) value);
            default:
                throw new IllegalArgumentException(
                        String.format("missing case statement for: %s", value.getClass().getName()));
        }
    }

    @Override
    public String toCqlId(IIdType value) {
        if (value == null) {
            return null;
        }

        return value.getIdPart();
    }

    @Override
    public Boolean toCqlBoolean(IPrimitiveType<Boolean> value) {
        if (value == null) {
            return null;
        }

        return value.getValue();
    }

    @Override
    public Integer toCqlInteger(IPrimitiveType<Integer> value) {
        if (value == null) {
            return null;
        }

        return value.getValue();
    }

    @Override
    public BigDecimal toCqlDecimal(IPrimitiveType<BigDecimal> value) {
        if (value == null) {
            return null;
        }

        return value.getValue();
    }

    @Override
    public Time toCqlTime(IPrimitiveType<String> value) {
        if (value == null) {
            return null;
        }

        return new Time(value.getValue());
    }

    @Override
    public String toCqlString(IPrimitiveType<String> value) {
       if (value == null) {
           return null;
       }

       return value.getValue();
    }


    @Override
    public Tuple toCqlTuple(IBase value) {
        if (value == null) {
            return null;
        }

        throw new NotImplementedException("toCqlTuple is not yet implemented");
    }

    protected String getSimpleName(String typeName) {
        String[] nameParts = typeName.split("\\.");
        return nameParts[nameParts.length-1];
    }

    protected Time toTime(Calendar calendar, Integer calendarConstant) {
        switch (calendarConstant) {
            case Calendar.HOUR: return new org.opencds.cqf.cql.engine.runtime.Time(calendar.get(Calendar.HOUR));
            case Calendar.MINUTE: return new org.opencds.cqf.cql.engine.runtime.Time(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
            case Calendar.SECOND: return new org.opencds.cqf.cql.engine.runtime.Time(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            case Calendar.MILLISECOND: return new org.opencds.cqf.cql.engine.runtime.Time(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
            default: throw new InvalidPrecision(String.format("Invalid temporal precision %s", calendarConstant));
        }
    }

    protected DateTime toDateTime(Calendar calendar, Integer calendarConstant) {
        TimeZone tz = calendar.getTimeZone() == null ? TimeZone.getDefault() : calendar.getTimeZone();
        ZoneOffset zoneOffset = tz.toZoneId().getRules().getStandardOffset(calendar.toInstant());
        switch (calendarConstant) {
            case Calendar.YEAR: return new DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR)
            );
            case Calendar.MONTH: return new DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1
            );
            case Calendar.DAY_OF_MONTH: return new DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)
            );
            case Calendar.HOUR_OF_DAY: return new DateTime(
                TemporalHelper.zoneToOffset(zoneOffset),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY)
            );
            case Calendar.MINUTE: return new DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
            );
            case Calendar.SECOND: return new DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)
            );
            case Calendar.MILLISECOND: return new DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND)
            );
            default: throw new InvalidPrecision(String.format("Invalid temporal precision %s", calendarConstant));
        }
    }

    protected org.opencds.cqf.cql.engine.runtime.Date toDate(Calendar calendar, Integer calendarConstant) {
        switch (calendarConstant) {
            case Calendar.YEAR: return new org.opencds.cqf.cql.engine.runtime.Date(calendar.get(Calendar.YEAR));
            case Calendar.MONTH: return new org.opencds.cqf.cql.engine.runtime.Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
            case Calendar.DAY_OF_MONTH: return new org.opencds.cqf.cql.engine.runtime.Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            default: throw new InvalidPrecision(String.format("Invalid temporal precision %s", calendarConstant));
        }
    }
}
