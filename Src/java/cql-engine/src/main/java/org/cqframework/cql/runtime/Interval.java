package org.cqframework.cql.runtime;

import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Created by Bryn on 4/15/2016.
 */
public class Interval {
    public Interval(Object low, boolean lowClosed, Object high, boolean highClosed) {
        this.low = low;
        this.lowClosed = lowClosed;
        this.high = high;
        this.highClosed = highClosed;

        if (this.low != null) {
            pointType = this.low.getClass();
        }
        else if (this.high != null) {
            pointType = this.high.getClass();
        }

        if (pointType == null) {
            throw new IllegalArgumentException("Low or high boundary of an interval must be present.");
        }

        if ((this.low != null && this.low.getClass() != pointType)
            || (this.high != null && this.high.getClass() != pointType)) {
            throw new IllegalArgumentException("Low and high boundary values of an interval must be of the same type.");
        }
    }

    private Object low;
    public Object getLow() {
        return low;
    }

    private boolean lowClosed;
    public boolean getLowClosed() {
        return lowClosed;
    }

    private Object high;
    public Object getHigh() {
        return high;
    }

    private boolean highClosed;
    public boolean getHighClosed() {
        return highClosed;
    }

    private Type pointType;

    /*
    Returns the starting point of the interval.

    If the low boundary of the interval is open, returns the Successor of the low value of the interval.
    Note that if the low value of the interval is null, the result is null.

    If the low boundary of the interval is closed and the low value of the interval is not null,
    returns the low value of the interval. Otherwise, the result is the minimum value of
    the point type of the interval.
     */
    public Object getStart() {
        if (!lowClosed) {
            return successor(low);
        }
        else {
            return low == null ? minValue(pointType) : low;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Interval)) {
            return false;
        }

        Interval otherInterval = (Interval)other;
        // TODO: Use Boolean to enable null propagation...
        return this.getLow() != null && this.getLow().equals(otherInterval.getLow())
                && this.getLowClosed() == otherInterval.getLowClosed()
                && this.getHigh() != null && this.getHigh().equals(otherInterval.getHigh())
                && this.getHighClosed() == otherInterval.getHighClosed();
    }

    /*
    Returns the ending point of an interval.

    If the high boundary of the interval is open, returns the Predecessor of the high value of the interval.
    Note that if the high value of the interval is null, the result is null.

    If the high boundary of the interval is closed and the high value of the interval is not null,
    returns the high value of the interval. Otherwise, the result is the maximum value of
    the point type of the interval.
     */
    public Object getEnd() {
        if (!highClosed) {
            return predecessor(high);
        }
        else {
            return high == null ? maxValue(pointType) : high;
        }
    }

    public static Object successor(Object value) {
        if (value == null) {
            return null;
        }
        else if (value instanceof Integer) {
            return ((Integer)value) + 1;
        }
        else if (value instanceof BigDecimal) {
            return ((BigDecimal)value).add(new BigDecimal("0.00000001"));
        }
        else if (value instanceof Quantity) {
            Quantity quantity = (Quantity)value;
            return new Quantity().withValue((BigDecimal)successor(quantity.getValue())).withUnit(quantity.getUnit());
        }
        else {
            // TODO: Implemented successor for DateTime and Time
            throw new NotImplementedException(String.format("Successor is not implemented for type %s", value.getClass().getName()));
        }
    }

    public static Object predecessor(Object value) {
        if (value == null) {
            return null;
        }
        else if (value instanceof Integer) {
            return ((Integer)value) - 1;
        }
        else if (value instanceof BigDecimal) {
            return ((BigDecimal)value).subtract(new BigDecimal("0.00000001"));
        }
        else if (value instanceof Quantity) {
            Quantity quantity = (Quantity)value;
            return new Quantity().withValue((BigDecimal)predecessor(quantity.getValue())).withUnit(quantity.getUnit());
        }
        else {
            // TODO: Implement predecessor for DateTime and Time
            throw new NotImplementedException(String.format("Predecessor is not implemented for type %s", value.getClass().getName()));
        }
    }

    public static Object minValue(Type type) {
        if (type == Integer.class) {
            return Integer.MIN_VALUE;
        }
        else if (type == BigDecimal.class) {
            return new BigDecimal("-9999999999999999999999999999.99999999");
        }
        else if (type == Quantity.class) {
            return new Quantity().withValue((BigDecimal)minValue(BigDecimal.class));
        }
        else {
            // TODO: Implement minValue for DateTime and Time
            throw new NotImplementedException(String.format("MinValue is not implemented for type %s.", type.getTypeName()));
        }
    }

    public static Object maxValue(Type type) {
        if (type == Integer.class) {
            return Integer.MAX_VALUE;
        }
        else if (type == BigDecimal.class) {
            return new BigDecimal("9999999999999999999999999999.99999999");
        }
        else if (type == Quantity.class) {
            return new Quantity().withValue((BigDecimal)maxValue(BigDecimal.class));
        }
        else {
            // TODO: Implement maxValue for DateTime and Time
            throw new NotImplementedException(String.format("MaxValue is not implemented for type %s.", type.getTypeName()));
        }
    }
}

