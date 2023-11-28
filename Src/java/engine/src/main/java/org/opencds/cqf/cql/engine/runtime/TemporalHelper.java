package org.opencds.cqf.cql.engine.runtime;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class TemporalHelper {

    private TemporalHelper() {
    }

    public static String[] normalizeDateTimeElements(int... elements) {
        String[] ret = new String[elements.length];
        for (int i = 0; i < elements.length; ++i) {
            switch (i) {
                case 0: ret[i] = addLeadingZeroes(elements[i], 4); break;
                case 6: ret[i] = addLeadingZeroes(elements[i], 3); break;
                default: ret[i] = addLeadingZeroes(elements[i], 2); break;
            }
        }

        return ret;
    }

    public static String[] normalizeTimeElements(int ... elements) {
        String[] ret = new String[elements.length];
        for (int i = 0; i < elements.length; ++i) {
            switch (i) {
                case 3: ret[i] = addLeadingZeroes(elements[i], 3); break;
                default: ret[i] = addLeadingZeroes(elements[i], 2); break;
            }
        }

        return ret;
    }

    public static String addLeadingZeroes(int element, int length) {
        String strElement = Integer.toString(element);
        return StringUtils.repeat("0", length - strElement.length()) + strElement;
    }

    public static String autoCompleteDateTimeString(String dateString, Precision precision) {
        switch (precision) {
            case YEAR: return dateString + "-01-01T00:00:00.000";
            case MONTH: return dateString + "-01T00:00:00.000";
            case DAY: return dateString + "T00:00:00.000";
            case HOUR: return dateString + ":00:00.000";
            case MINUTE: return dateString + ":00.000";
            case SECOND: return dateString + ".000";
            default: return dateString;
        }
    }

    public static String autoCompleteDateString(String dateString, Precision precision) {
        switch (precision) {
            case YEAR: return dateString + "-01-01";
            case MONTH: return dateString + "-01";
            default: return dateString;
        }
    }

    public static String autoCompleteTimeString(String timeString, Precision precision) {
        switch (precision) {
            case HOUR:
            case MINUTE: return timeString + ":00.000";
            case SECOND: return timeString + ".000";
            default: return timeString;
        }
    }

    public static int[] cleanArray(Integer ... elements) {
        return Arrays.stream(elements).filter(Objects::nonNull).mapToInt(x -> x).toArray();
    }

    public static BigDecimal zoneToOffset(ZoneOffset zone) {
        int seconds = zone.get(ChronoField.OFFSET_SECONDS);
        return new BigDecimal(Double.toString(seconds/60f/60f));
    }

    public static int weeksToDays(int weeks) {
        int years = 0;
        if (weeks >= 52) {
            years = (weeks / 52);
            weeks -= years * 52 ;
        }
        return weeks * 7 + (years * 365);
    }

    public static long truncateValueToTargetPrecision(long value, Precision precision, Precision targetPrecision) {
        if (targetPrecision == Precision.YEAR) {
            switch (precision) {
                case YEAR:
                    return value;
                case MONTH:
                    return value / 12;
                case DAY:
                    return value / 365;
                case HOUR:
                    return value / (24 * 365);
                case MINUTE:
                    return value / (24 * 365 * 60);
                case SECOND:
                    return value / (24 * 365 * 60 * 60);
                case MILLISECOND:
                    return ((value / 1000) / (3600)) / (24 * 365);
            }

        } else if (targetPrecision == Precision.MONTH) {
            switch (precision) {
                case YEAR:
                    return value * 12;
                case MONTH:
                    return value;
                case DAY:
                    return value / 30;
                case HOUR:
                    return value / (30 * 24);
                case MINUTE:
                    return value / (30 * 24 * 60);
                case SECOND:
                    return value / (30 * 24 * 60 * 60);
                case MILLISECOND:
                    return ((value / 1000) / (3600)) / (30 * 24);
            }
        } else if (targetPrecision == Precision.DAY) {
            switch (precision) {
                case YEAR:
                    return value * 365;
                case MONTH:
                    return value * 12;
                case DAY:
                    return value;
                case HOUR:
                    return value / 24;
                case MINUTE:
                    return value / (24 * 60);
                case SECOND:
                    return value / (24 * 60 * 60);
                case MILLISECOND:
                    return ((value / 1000) / (3600)) / 24;
            }

        } else if (targetPrecision == Precision.HOUR) {
            switch (precision) {
                case YEAR:
                    return value * 365 * 24;
                case MONTH:
                    return value * 30 * 24;
                case DAY:
                    return value * 24;
                case HOUR:
                    return value;
                case MINUTE:
                    return value / 60;
                case SECOND:
                    return value / (60 * 60);
                case MILLISECOND:
                    return (value / 1000) / 3600;
            }
        } else if (targetPrecision == Precision.MINUTE) {
            switch (precision) {
                case YEAR:
                    return value * 365 * 24 * 60;
                case MONTH:
                    return value * 30 * 24 * 60;
                case DAY:
                    return value * 24 * 60;
                case HOUR:
                    return value * 60;
                case MINUTE:
                    return value;
                case SECOND:
                    return value / 60;
                case MILLISECOND:
                    return (value / 1000) / 60;
            }
        } else if (targetPrecision == Precision.SECOND) {
            switch (precision) {
                case YEAR:
                    return value * 365 * 24 * 60 * 60;
                case MONTH:
                    return value * 30 * 24 * 60 * 60;
                case DAY:
                    return value * 24 * 60 * 60;
                case HOUR:
                    return value * 60 * 60;
                case MINUTE:
                    return value * 60;
                case SECOND:
                    return value;
                case MILLISECOND:
                    return value / 1000;
            }
        } else if (targetPrecision == Precision.MILLISECOND) {
            switch (precision) {
                case YEAR:
                    return value * 365 * 24 * 60 * 60 * 1000;
                case MONTH:
                    return value * 30 * 24 * 60 * 60;
                case DAY:
                    return value * 24 * 60 * 60;
                case HOUR:
                    return value * 60 * 60;
                case MINUTE:
                    return value * 60;
                case SECOND:
                    return value;
                case MILLISECOND:
                    return value / 1000;
            }
        }
        return 0;
    }
}
