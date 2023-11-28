package org.opencds.cqf.cql.engine.runtime;

import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.InvalidDateTime;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class DateTime extends BaseTemporal {
    private ZoneOffset zoneOffset;

    private OffsetDateTime dateTime;
    public OffsetDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(OffsetDateTime dateTime) {
        if (dateTime.getYear() < 1) {
            throw new InvalidDateTime(String.format("The year: %d falls below the accepted bounds of 0001-9999.", dateTime.getYear()));
        }

        if (dateTime.getYear() > 9999) {
            throw new InvalidDateTime(String.format("The year: %d falls above the accepted bounds of 0001-9999.", dateTime.getYear()));
        }
        this.dateTime = dateTime;
    }

    public DateTime withPrecision(Precision precision) {
        this.precision = precision;
        return this;
    }

    public DateTime(OffsetDateTime dateTime) {
        setDateTime(dateTime);
        this.precision = Precision.MILLISECOND;
        zoneOffset = toZoneOffset(dateTime);
    }

    public DateTime(OffsetDateTime dateTime, Precision precision) {
        setDateTime(dateTime);
        this.precision = precision;
        zoneOffset = toZoneOffset(dateTime);
    }

    public DateTime(String dateString, ZoneOffset offset) {
        if (offset == null) {
            throw new CqlException("Cannot pass a null offset");
        }

        zoneOffset = offset;

        // Handles case when Tz is not complete (T02:04:59.123+01)
        if (dateString.matches("T[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d{3}(\\+|-)\\d{2}$")) {
            dateString += ":00";
        }
        int size = 0;
        if (dateString.contains("T")) {
            String[] datetimeSplit = dateString.split("T");
            size += datetimeSplit[0].split("-").length;
            String[] tzSplit = dateString.contains("Z") ? dateString.split("Z") : datetimeSplit[1].split("[+-]");
            size += tzSplit[0].split(":").length;
            if (tzSplit[0].contains(".")) {
                ++size;
            }
            precision = Precision.fromDateTimeIndex(size - 1);
            if (tzSplit.length == 1 && !dateString.contains("Z")) {
                dateString = TemporalHelper.autoCompleteDateTimeString(dateString, precision);
                dateString += offset.getId();
            }
        }
        else {
            size += dateString.split("-").length;
            precision = Precision.fromDateTimeIndex(size - 1);
            dateString = TemporalHelper.autoCompleteDateTimeString(dateString, precision);
            dateString += offset.getId();
        }

        setDateTime(OffsetDateTime.parse(dateString));
    }

    public DateTime(BigDecimal offset, int ... dateElements) {
        if (offset == null) {
            throw new CqlException("BigDecimal offset must be non-null");
        }

        if (dateElements.length == 0) {
            throw new InvalidDateTime("DateTime must include a year");
        }

        zoneOffset = toZoneOffset(offset);

        StringBuilder dateString = new StringBuilder();
        String[] stringElements = TemporalHelper.normalizeDateTimeElements(dateElements);

        for (int i = 0; i < stringElements.length; ++i) {
            if (i == 0) {
                dateString.append(stringElements[i]);
                continue;
            }
            else if (i < 3) {
                dateString.append("-");
            }
            else if (i == 3) {
                dateString.append("T");
            }
            else if (i < 6) {
                dateString.append(":");
            }
            else if (i == 6) {
                dateString.append(".");
            }
            dateString.append(stringElements[i]);
        }

        precision = Precision.fromDateTimeIndex(stringElements.length - 1);
        dateString = new StringBuilder().append(TemporalHelper.autoCompleteDateTimeString(dateString.toString(), precision));
        dateString.append(zoneOffset.getId());
        setDateTime(OffsetDateTime.parse(dateString.toString()));
    }

    public ZoneOffset getZoneOffset() {
        return zoneOffset;
    }

    public DateTime expandPartialMinFromPrecision(Precision thePrecision) {
        OffsetDateTime odt = this.getDateTime().plusYears(0);
        for (int i = thePrecision.toDateTimeIndex() + 1; i < 7; ++i) {
            odt = odt.with(
                    Precision.fromDateTimeIndex(i).toChronoField(),
                    odt.range(Precision.fromDateTimeIndex(i).toChronoField()).getMinimum()
            );
        }
        return new DateTime(odt, this.precision);
    }

    public DateTime expandPartialMin(Precision thePrecision) {
        OffsetDateTime odt = this.getDateTime().plusYears(0);
        return new DateTime(odt, thePrecision == null ? Precision.MILLISECOND : thePrecision);
    }

    public DateTime expandPartialMax(Precision thePrecision) {
        OffsetDateTime odt = this.getDateTime().plusYears(0);
        for (int i = this.getPrecision().toDateTimeIndex() + 1; i < 7; ++i) {
            if (i <= thePrecision.toDateTimeIndex()) {
                odt = odt.with(
                        Precision.fromDateTimeIndex(i).toChronoField(),
                        odt.range(Precision.fromDateTimeIndex(i).toChronoField()).getMaximum()
                );
            }
            else {
                odt = odt.with(
                        Precision.fromDateTimeIndex(i).toChronoField(),
                        odt.range(Precision.fromDateTimeIndex(i).toChronoField()).getMinimum()
                );
            }
        }
        return new DateTime(odt, thePrecision == null ? Precision.MILLISECOND : thePrecision);
    }

    @Override
    public boolean isUncertain(Precision thePrecision) {
        if (thePrecision == Precision.WEEK) {
            thePrecision = Precision.DAY;
        }

        return this.precision.toDateTimeIndex() < thePrecision.toDateTimeIndex();
    }

    @Override
    public Interval getUncertaintyInterval(Precision thePrecision) {
        DateTime start = expandPartialMin(thePrecision);
        DateTime end = expandPartialMax(thePrecision).expandPartialMinFromPrecision(thePrecision);
        return new Interval(start, true, end, true);
    }

    @Override
    public Integer compare(BaseTemporal other, boolean forSort) {
        boolean differentPrecisions = this.getPrecision() != other.getPrecision();

        if (differentPrecisions) {
            Integer result = this.compareToPrecision(other, Precision.getHighestDateTimePrecision(this.precision, other.precision));
            if (result == null && forSort) {
                return this.precision.toDateTimeIndex() > other.precision.toDateTimeIndex() ? 1 : -1;
            }
            return result;
        }
        else {
            return compareToPrecision(other, this.precision);
        }
    }

    public OffsetDateTime getNormalized(Precision precision) {
        return getNormalized(precision, zoneOffset);
    }

    public OffsetDateTime getNormalized(Precision precision, ZoneOffset nullableZoneOffset) {
        if (precision.toDateTimeIndex() > Precision.DAY.toDateTimeIndex()) {
            if (nullableZoneOffset != null) {
                return dateTime.withOffsetSameInstant(nullableZoneOffset);
            }

            throw new IllegalStateException("There must be a non-null offset!");
        }

        return dateTime;
    }

    @Override
    public Integer compareToPrecision(BaseTemporal other, Precision thePrecision) {
        boolean leftMeetsPrecisionRequirements = this.precision.toDateTimeIndex() >= thePrecision.toDateTimeIndex();
        boolean rightMeetsPrecisionRequirements = other.precision.toDateTimeIndex() >= thePrecision.toDateTimeIndex();

        // adjust dates to evaluation offset
        OffsetDateTime leftDateTime = this.getNormalized(thePrecision);
        OffsetDateTime rightDateTime = ((DateTime) other).getNormalized(thePrecision, getZoneOffset());

        if (!leftMeetsPrecisionRequirements || !rightMeetsPrecisionRequirements) {
            thePrecision = Precision.getLowestDateTimePrecision(this.precision, other.precision);
        }

        for (int i = 0; i < thePrecision.toDateTimeIndex() + 1; ++i) {
            int leftComp = leftDateTime.get(Precision.getDateTimeChronoFieldFromIndex(i));
            int rightComp = rightDateTime.get(Precision.getDateTimeChronoFieldFromIndex(i));
            if (leftComp > rightComp) {
                return 1;
            }
            else if (leftComp < rightComp) {
                return -1;
            }
        }

        if (leftMeetsPrecisionRequirements && rightMeetsPrecisionRequirements) {
            return 0;
        }

        return null;
    }

    @Override
    public int compareTo(BaseTemporal other) {
        return this.compare(other, true);
    }

    @Override
    public Boolean equivalent(Object other) {
        Integer comparison = compare((BaseTemporal) other, false);
        return comparison != null && comparison == 0;
    }

    @Override
    public Boolean equal(Object other) {
        Integer comparison = compare((BaseTemporal) other, false);
        return comparison == null ? null : comparison == 0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final DateTime otherDateTime = (DateTime) other;

        return Objects.equals(zoneOffset, otherDateTime.zoneOffset) &&
               Objects.equals(precision, otherDateTime.precision) &&
               Objects.equals(dateTime, otherDateTime.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zoneOffset, dateTime);
    }

    @Override
    public String toString() {
        switch (precision) {
            case YEAR: return String.format("%04d", dateTime.getYear());
            case MONTH: return String.format("%04d-%02d", dateTime.getYear(), dateTime.getMonthValue());
            case DAY: return String.format("%04d-%02d-%02d", dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
            case HOUR: return String.format("%04d-%02d-%02dT%02d", dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour());
            case MINUTE: return String.format("%04d-%02d-%02dT%02d:%02d", dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute());
            case SECOND: return String.format("%04d-%02d-%02dT%02d:%02d:%02d", dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
            default: return String.format("%04d-%02d-%02dT%02d:%02d:%02d.%03d", dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond(), dateTime.get(precision.toChronoField()));
        }
    }

    // conversion functions

    public Date toJavaDate() {
        return java.util.Date.from(dateTime.toInstant());
    }

    public String toDateString() {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime);
    }

    private ZoneOffset toZoneOffset(OffsetDateTime offsetDateTime) {
        return offsetDateTime.getOffset();
    }

    private ZoneOffset toZoneOffset(BigDecimal offsetAsBigDecimal) {
        if (offsetAsBigDecimal == null) {
            return null;
        }

        return ZoneOffset.ofHoursMinutes(offsetAsBigDecimal.intValue(),
                new BigDecimal(60)
                .multiply(offsetAsBigDecimal.remainder(BigDecimal.ONE))
                        .intValue());
    }
}
