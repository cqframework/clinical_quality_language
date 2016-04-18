package org.cqframework.cql.runtime;

/**
 * Created by Bryn on 4/15/2016.
 */
public class Interval<T> {
    public Interval(T low, boolean lowClosed, T high, boolean highClosed) {
        this.low = low;
        this.lowClosed = lowClosed;
        this.high = high;
        this.highClosed = highClosed;
    }

    private T low;
    public T getLow() {
        return low;
    }

    private boolean lowClosed;
    public boolean getLowClosed() {
        return lowClosed;
    }

    private T high;
    public T getHigh() {
        return high;
    }

    private boolean highClosed;
    public boolean getHighClosed() {
        return highClosed;
    }
}
