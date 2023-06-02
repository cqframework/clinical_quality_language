package org.opencds.cqf.cql.engine.runtime;

import java.time.LocalTime;



import org.opencds.cqf.cql.engine.exception.InvalidTime;

public class Time extends BaseTemporal {

    private LocalTime time;
    public LocalTime getTime() {
        return time;
    }

    public Time withTime(LocalTime time) {
        this.time = time;
        return this;
    }

    public Time withPrecision(Precision precision) {
        this.precision = precision;
        return this;
    }

    public Time(LocalTime time, Precision precision) {
        this.time = time;
        this.precision = precision;
    }

    public Time(String dateString) {
        int size = 0;
        if (dateString.matches("^T[0-2]\\d$") || dateString.matches("^[0-2]\\d$")) {
            dateString += ":00";
            size = -1;
        }
        dateString = dateString.replace("T", "");
        size += dateString.split(":").length;
        if (dateString.contains(".")) {
            ++size;
        }
        precision = Precision.fromTimeIndex(size - 1);
        dateString = TemporalHelper.autoCompleteTimeString(dateString, precision);
        time = LocalTime.parse(dateString);
    }

    public Time(int ... timeElements) {
        if (timeElements.length == 0) {
            throw new InvalidTime("Time must include an hour");
        }

        StringBuilder timeString = new StringBuilder();
        String[] stringElements = TemporalHelper.normalizeTimeElements(timeElements);

        for (int i = 0; i < stringElements.length; ++i) {
            if (i == 0) {
                timeString.append(stringElements[i]);
                continue;
            }
            else if (i < 3) {
                timeString.append(":");
            }
            else if (i == 3) {
                timeString.append(".");
            }
            timeString.append(stringElements[i]);
        }

        precision = Precision.fromTimeIndex(stringElements.length - 1);
        timeString = new StringBuilder().append(TemporalHelper.autoCompleteDateTimeString(timeString.toString(), precision));

        time = LocalTime.parse(timeString.toString());
    }

    public Time expandPartialMinFromPrecision(Precision thePrecision) {
        LocalTime ot = this.time.plusHours(0);
        for (int i = thePrecision.toTimeIndex() + 1; i < 4; ++i) {
            ot = ot.with(
                    Precision.fromTimeIndex(i).toChronoField(),
                    ot.range(Precision.fromTimeIndex(i).toChronoField()).getMinimum()
            );
        }
        return new Time(ot, this.precision);
    }

    public Time expandPartialMin(Precision thePrecision) {
        LocalTime ot = this.getTime().plusHours(0);
        return new Time(ot, thePrecision == null ? Precision.MILLISECOND : thePrecision);
    }

    public Time expandPartialMax(Precision thePrecision) {
        LocalTime ot = this.getTime().plusHours(0);
        for (int i = this.getPrecision().toTimeIndex() + 1; i < 4; ++i) {
            if (i <= thePrecision.toTimeIndex()) {
                ot = ot.with(
                        Precision.fromTimeIndex(i).toChronoField(),
                        ot.range(Precision.fromTimeIndex(i).toChronoField()).getMaximum()
                );
            }
            else {
                ot = ot.with(
                        Precision.fromTimeIndex(i).toChronoField(),
                        ot.range(Precision.fromTimeIndex(i).toChronoField()).getMinimum()
                );
            }
        }
        return new Time(ot, thePrecision == null ? Precision.MILLISECOND : thePrecision);
    }

    @Override
    public boolean isUncertain(Precision thePrecision) {
        return this.precision.toTimeIndex() < thePrecision.toTimeIndex();
    }

    @Override
    public Interval getUncertaintyInterval(Precision thePrecision) {
        Time start = expandPartialMin(thePrecision);
        Time end = expandPartialMax(thePrecision).expandPartialMinFromPrecision(thePrecision);
        return new Interval(start, true, end, true);
    }

    @Override
    public Integer compare(BaseTemporal other, boolean forSort) {
        boolean differentPrecisions = this.getPrecision() != other.getPrecision();

        if (differentPrecisions) {
            Integer result = this.compareToPrecision(other, Precision.getHighestTimePrecision(this.precision, other.precision));
            if (result == null && forSort) {
                return this.precision.toTimeIndex() > other.precision.toTimeIndex() ? 1 : -1;
            }
            return result;
        }
        else {
            return compareToPrecision(other, this.precision);
        }
    }

    @Override
    public Integer compareToPrecision(BaseTemporal other, Precision thePrecision) {
        boolean leftMeetsPrecisionRequirements = this.precision.toTimeIndex() >= thePrecision.toTimeIndex();
        boolean rightMeetsPrecisionRequirements = other.precision.toTimeIndex() >= thePrecision.toTimeIndex();

        // adjust dates to evaluation offset
        LocalTime leftTime = this.time;
        LocalTime rightTime = ((Time) other).time;

        if (!leftMeetsPrecisionRequirements || !rightMeetsPrecisionRequirements) {
            thePrecision = Precision.getLowestTimePrecision(this.precision, other.precision);
        }

        for (int i = 0; i < thePrecision.toTimeIndex() + 1; ++i) {
            int leftComp = leftTime.get(Precision.getTimeChronoFieldFromIndex(i));
            int rightComp = rightTime.get(Precision.getTimeChronoFieldFromIndex(i));
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
    public String toString() {
        switch (precision) {
            case HOUR: return String.format("%02d", time.getHour());
            case MINUTE: return String.format("%02d:%02d", time.getHour(), time.getMinute());
            case SECOND: return String.format("%02d:%02d:%02d", time.getHour(), time.getMinute(), time.getSecond());
            default: return String.format("%02d:%02d:%02d.%03d", time.getHour(), time.getMinute(), time.getSecond(), time.get(precision.toChronoField()));
        }
    }
}
