package org.cqframework.cql.poc.interpreter;

import java.util.Calendar;
import java.util.Date;

public class MeasurePeriod {
    private final Date start;
    private final Date stop;

    public MeasurePeriod(Date start, Date stop) {
        this.start = start;
        this.stop = stop;
    }

    public static MeasurePeriod forYear(int year) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, Calendar.JANUARY);
        a.set(Calendar.DAY_OF_MONTH, 1);
        a.set(Calendar.HOUR_OF_DAY, 0);
        a.set(Calendar.MINUTE, 0);
        a.set(Calendar.SECOND, 0);
        a.set(Calendar.MILLISECOND, 0);

        Calendar b = Calendar.getInstance();
        b.set(Calendar.YEAR, year);
        b.set(Calendar.MONTH, Calendar.DECEMBER);
        b.set(Calendar.DAY_OF_MONTH, 31);
        b.set(Calendar.HOUR_OF_DAY, 23);
        b.set(Calendar.MINUTE, 59);
        b.set(Calendar.SECOND, 59);
        b.set(Calendar.MILLISECOND, 999);

        return new MeasurePeriod(a.getTime(), b.getTime());
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return stop;
    }

    @Override
    public String toString() {
        return "MeasurePeriod{" +
                "start=" + start +
                ", stop=" + stop +
                '}';
    }
}
