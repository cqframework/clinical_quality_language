package org.opencds.cqf.cql.engine.runtime;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import org.opencds.cqf.cql.engine.exception.InvalidPrecision;

public enum Precision {
    YEAR,
    MONTH,
    WEEK,
    DAY,
    HOUR,
    MINUTE,
    SECOND,
    MILLISECOND;

    public ChronoField toChronoField() {
        switch (this) {
            case YEAR: return ChronoField.YEAR;
            case MONTH: return ChronoField.MONTH_OF_YEAR;
            case DAY: return ChronoField.DAY_OF_MONTH;
            case HOUR: return ChronoField.HOUR_OF_DAY;
            case MINUTE: return ChronoField.MINUTE_OF_HOUR;
            case SECOND: return ChronoField.SECOND_OF_MINUTE;
            default: return ChronoField.MILLI_OF_SECOND;
        }
    }

    public ChronoUnit toChronoUnit() {
        switch (this) {
            case YEAR: return ChronoUnit.YEARS;
            case MONTH: return ChronoUnit.MONTHS;
            case DAY: return ChronoUnit.DAYS;
            case HOUR: return ChronoUnit.HOURS;
            case MINUTE: return ChronoUnit.MINUTES;
            case SECOND: return ChronoUnit.SECONDS;
            default: return ChronoUnit.MILLIS;
        }
    }

    public int toDateIndex() {
        switch (this) {
            case YEAR: return 0;
            case MONTH: return 1;
            default: return 2;
        }
    }

    public int toDateTimeIndex() {
        switch (this) {
            case YEAR: return 0;
            case MONTH: return 1;
            case DAY: return 2;
            case HOUR: return 3;
            case MINUTE: return 4;
            case SECOND: return 5;
            default: return 6;
        }
    }

    public int toTimeIndex() {
        switch (this) {
            case HOUR: return 0;
            case MINUTE: return 1;
            case SECOND: return 2;
            default: return 3;
        }
    }

    public Precision getNextPrecision() {
        switch (this) {
            case YEAR: return MONTH;
            case MONTH: return DAY;
            case DAY: return HOUR;
            case HOUR: return MINUTE;
            case MINUTE: return SECOND;
            case SECOND: return MILLISECOND;
            default: return MILLISECOND;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case YEAR: return "year";
            case MONTH: return "month";
            case WEEK: return "week";
            case DAY: return "day";
            case HOUR: return "hour";
            case MINUTE: return "minute";
            case SECOND: return "second";
            default: return "millisecond";
        }
    }

    public static Precision fromString(String precision) {
        precision = precision.toLowerCase();
        if (precision.startsWith("year")) {
            return YEAR;
        }
        else if (precision.startsWith("month")) {
            return MONTH;
        }
        else if (precision.startsWith("day")) {
            return DAY;
        }
        else if (precision.startsWith("week")) {
            return WEEK;
        }
        else if (precision.startsWith("hour")) {
            return HOUR;
        }
        else if (precision.startsWith("minute")) {
            return MINUTE;
        }
        else if (precision.startsWith("second")) {
            return SECOND;
        }
        else if (precision.startsWith("millisecond")) {
            return MILLISECOND;
        }

        throw new InvalidPrecision("Invalid precision: " + precision);
    }

    public static Precision fromDateIndex(int index) {
        switch (index) {
            case 0: return YEAR;
            case 1: return MONTH;
            case 2: return DAY;
            default: throw new InvalidPrecision("Invalid precision index: " + Integer.toString(index));
        }
    }

    public static Precision fromDateTimeIndex(int index) {
        switch (index) {
            case 0: return YEAR;
            case 1: return MONTH;
            case 2: return DAY;
            case 3: return HOUR;
            case 4: return MINUTE;
            case 5: return SECOND;
            case 6: return MILLISECOND;
            default: throw new InvalidPrecision("Invalid precision index: " + Integer.toString(index));
        }
    }

    public static Precision fromTimeIndex(int index) {
        return fromDateTimeIndex(index + 3);
    }

    public static ChronoField getDateChronoFieldFromIndex(int index) {
        switch (index) {
            case 0: return ChronoField.YEAR;
            case 1: return ChronoField.MONTH_OF_YEAR;
            case 2: return ChronoField.DAY_OF_MONTH;
            default: throw new InvalidPrecision("Invalid precision index: " + Integer.toString(index));
        }
    }

    public static ChronoField getDateTimeChronoFieldFromIndex(int index) {
        switch (index) {
            case 0: return ChronoField.YEAR;
            case 1: return ChronoField.MONTH_OF_YEAR;
            case 2: return ChronoField.DAY_OF_MONTH;
            case 3: return ChronoField.HOUR_OF_DAY;
            case 4: return ChronoField.MINUTE_OF_HOUR;
            case 5: return ChronoField.SECOND_OF_MINUTE;
            case 6: return ChronoField.MILLI_OF_SECOND;
            default: throw new InvalidPrecision("Invalid precision index: " + Integer.toString(index));
        }
    }

    public static ChronoField getTimeChronoFieldFromIndex(int index) {
        return getDateTimeChronoFieldFromIndex(index + 3);
    }

    public static Precision getLowestDatePrecision(Precision p1, Precision p2) {
        return p1.toDateIndex() < p2.toDateIndex() ? p1 : p2;
    }

    public static Precision getHighestDatePrecision(Precision p1, Precision p2) {
        return p1.toDateIndex() > p2.toDateIndex() ? p1 : p2;
    }

    public static Precision getLowestDateTimePrecision(Precision p1, Precision p2) {
        return p1.toDateTimeIndex() < p2.toDateTimeIndex() ? p1 : p2;
    }

    public static Precision getHighestDateTimePrecision(Precision p1, Precision p2) {
        return p1.toDateTimeIndex() > p2.toDateTimeIndex() ? p1 : p2;
    }

    public static Precision getLowestTimePrecision(Precision p1, Precision p2) {
        return p1.toTimeIndex() < p2.toTimeIndex() ? p1 : p2;
    }

    public static Precision getHighestTimePrecision(Precision p1, Precision p2) {
        return p1.toTimeIndex() > p2.toTimeIndex() ? p1 : p2;
    }
}
