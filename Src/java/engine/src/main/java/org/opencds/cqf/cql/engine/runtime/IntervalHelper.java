package org.opencds.cqf.cql.engine.runtime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.opencds.cqf.cql.engine.elm.executing.LessOrEqualEvaluator;
import org.opencds.cqf.cql.engine.exception.InvalidPrecision;
import org.opencds.cqf.cql.engine.execution.State;

public class IntervalHelper {

    /**
     * Returns the first non-null boundary from the list of intervals.
     *
     * @param intervals the list of intervals to search
     * @return the first non-null boundary found
     */
    public static Object findNonNullBoundary(List<Interval> intervals) {
        return intervals.stream()
                .filter(Objects::nonNull)
                .flatMap(interval -> Stream.of(interval.getStart(), interval.getEnd()))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates a Quantity based on the coarsest precision of the boundaries from the given intervals.
     *
     * @param intervals the list of intervals to use
     * @return a Quantity with a value of 1 and the scale and unit determined by the precision of the interval boundaries
     */
    public static Quantity quantityFromCoarsestPrecisionOfBoundaries(List<Interval> intervals) {
        var nonNullBoundary = findNonNullBoundary(intervals);

        if (nonNullBoundary instanceof BigDecimal) {
            var scale = Value.getCoarsestScale(intervals.stream()
                    .filter(Objects::nonNull)
                    .flatMap(interval -> Stream.of((BigDecimal) interval.getStart(), (BigDecimal) interval.getEnd())));
            return new Quantity()
                    .withValue(BigDecimal.ONE.setScale(scale, RoundingMode.UNNECESSARY))
                    .withDefaultUnit();
        } else if (nonNullBoundary instanceof Quantity) {
            var scale = Value.getCoarsestScale(intervals.stream()
                    .filter(Objects::nonNull)
                    .flatMap(interval -> Stream.of(((Quantity) interval.getStart()), ((Quantity) interval.getEnd())))
                    .filter(Objects::nonNull)
                    .map(Quantity::getValue));
            return new Quantity()
                    .withValue(BigDecimal.ONE.setScale(scale, RoundingMode.UNNECESSARY))
                    .withUnit(((Quantity) nonNullBoundary).getUnit());
        } else if (nonNullBoundary instanceof BaseTemporal) {
            var precision = BaseTemporal.getLowestPrecision(intervals.stream()
                    .filter(Objects::nonNull)
                    .flatMap(
                            interval -> Stream.of((BaseTemporal) interval.getStart(), (BaseTemporal) interval.getEnd()))
                    .toArray(BaseTemporal[]::new));
            return new Quantity().withValue(BigDecimal.ONE).withUnit(precision);
        } else {
            return new Quantity().withValue(BigDecimal.ONE).withDefaultUnit();
        }
    }

    /**
     * Checks if the given quantity is compatible with the boundaries of the intervals.
     *
     * @param quantity the quantity to check
     * @param intervals the list of intervals to check against
     * @return true if the quantity is compatible with the boundaries, false otherwise
     */
    public static boolean isQuantityCompatibleWithBoundaries(Quantity quantity, List<Interval> intervals) {
        var nonNullBoundary = findNonNullBoundary(intervals);

        if (nonNullBoundary instanceof Integer
                || nonNullBoundary instanceof Long
                || nonNullBoundary instanceof BigDecimal) {
            return Quantity.isDefaultUnit(quantity.getUnit());
        } else if (nonNullBoundary instanceof Quantity) {
            return Quantity.unitsEqual(quantity.getUnit(), ((Quantity) nonNullBoundary).getUnit());
        } else if (nonNullBoundary instanceof BaseTemporal) {
            try {
                Precision.fromString(quantity.getUnit());
                return true;
            } catch (InvalidPrecision e) {
                return false; // quantity unit is not a valid temporal unit
            }
        } else {
            return true;
        }
    }

    /**
     * Truncates the boundaries of the given interval to the precision specified by the given quantity. When the
     * boundaries are truncated, the truncated start is rounded towards positive infinity and the truncated end is
     * rounded towards negative infinity. If the truncated start becomes greater than the truncated end (e.g. for
     * interval = Interval[0.3, 0.5] and quantity = 1 '1'), this method returns null.
     *
     * @param interval the interval with the boundaries to truncate
     * @param quantity the quantity specifying the precision to truncate to
     * @param state the engine state
     * @return the interval with the truncated boundaries
     */
    public static Interval truncateIntervalBoundaries(Interval interval, Quantity quantity, State state) {
        var start = interval.getStart();
        var end = interval.getEnd();

        if (start instanceof BigDecimal) {
            var quantityScale = quantity.getValue().scale();
            var truncatedStart = Value.roundToScale((BigDecimal) start, quantityScale, true);
            var truncatedEnd = Value.roundToScale((BigDecimal) end, quantityScale, false);

            if (truncatedStart.compareTo(truncatedEnd) <= 0) {
                return new Interval(truncatedStart, true, truncatedEnd, true);
            }

            return null;
        } else if (start instanceof Quantity) {
            var quantityScale = quantity.getValue().scale();
            var truncatedStart = new Quantity()
                    .withValue(Value.roundToScale(((Quantity) start).getValue(), quantityScale, true))
                    .withUnit(((Quantity) start).getUnit());
            var truncatedEnd = new Quantity()
                    .withValue(Value.roundToScale(((Quantity) end).getValue(), quantityScale, false))
                    .withUnit(((Quantity) end).getUnit());

            if (truncatedStart.compareTo(truncatedEnd) <= 0) {
                return new Interval(truncatedStart, true, truncatedEnd, true);
            }

            return null;
        } else if (start instanceof BaseTemporal) {
            var precision = Precision.fromString(quantity.getUnit());
            if (precision == Precision.WEEK) {
                precision = Precision.DAY;
            }
            var truncatedStart = ((BaseTemporal) start).roundToPrecision(precision, true);
            var truncatedEnd = ((BaseTemporal) end).roundToPrecision(precision, false);

            if (Boolean.TRUE.equals(LessOrEqualEvaluator.lessOrEqual(truncatedStart, truncatedEnd, state))) {
                return new Interval(truncatedStart, true, truncatedEnd, true);
            }

            return null;
        }

        return interval;
    }
}
