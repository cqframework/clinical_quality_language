package org.opencds.cqf.cql.engine.runtime;

public abstract class BaseTemporal implements CqlType, Comparable<BaseTemporal> {

    Precision precision;
    public Precision getPrecision() {
        return precision;
    }
    public BaseTemporal setPrecision(Precision precision) {
        this.precision = precision;
        return this;
    }

    public static String getHighestPrecision(BaseTemporal ... values) {
        int max = -1;
        boolean isDateTime = true;
        boolean isDate = false;
        for (BaseTemporal baseTemporal : values) {
            if (baseTemporal instanceof DateTime) {
                if (baseTemporal.precision.toDateTimeIndex() > max) {
                    max = ((DateTime) baseTemporal).precision.toDateTimeIndex();
                }
            }
            else if (baseTemporal instanceof Date) {
                isDateTime = false;
                isDate = true;
                if (baseTemporal.precision.toTimeIndex() > max) {
                    max = ((Date) baseTemporal).precision.toDateIndex();
                }
            }
            else if (baseTemporal instanceof Time) {
                isDateTime = false;
                if (baseTemporal.precision.toTimeIndex() > max) {
                    max = ((Time) baseTemporal).precision.toTimeIndex();
                }
            }
        }

        if (max == -1) {
            return Precision.MILLISECOND.toString();
        }

        return isDateTime ? Precision.fromDateTimeIndex(max).toString() : isDate ? Precision.fromDateIndex(max).toString() : Precision.fromTimeIndex(max).toString();
    }

    public static String getLowestPrecision(BaseTemporal ... values) {
        int min = 99;
        boolean isDateTime = true;
        boolean isDate = false;
        for (BaseTemporal baseTemporal : values) {
            if (baseTemporal instanceof DateTime) {
                if (baseTemporal.precision.toDateTimeIndex() < min) {
                    min = ((DateTime) baseTemporal).precision.toDateTimeIndex();
                }
            }
            else if (baseTemporal instanceof Date) {
                isDateTime = false;
                isDate = true;
                if (baseTemporal.precision.toTimeIndex() < min) {
                    min = ((Date) baseTemporal).precision.toDateIndex();
                }
            }
            else if (baseTemporal instanceof Time) {
                isDateTime = false;
                if (baseTemporal.precision.toTimeIndex() < min) {
                    min = ((Time) baseTemporal).precision.toTimeIndex();
                }
            }
        }

        if (min == 99) {
            return Precision.YEAR.toString();
        }

        return isDateTime ? Precision.fromDateTimeIndex(min).toString() : isDate ? Precision.fromDateIndex(min).toString() : Precision.fromTimeIndex(min).toString();
    }

    public abstract Integer compare(BaseTemporal other, boolean forSort);
    public abstract Integer compareToPrecision(BaseTemporal other, Precision p);
    public abstract boolean isUncertain(Precision p);
    public abstract Interval getUncertaintyInterval(Precision p);
}
