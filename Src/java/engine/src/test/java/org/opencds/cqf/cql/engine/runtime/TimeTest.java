package org.opencds.cqf.cql.engine.runtime;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class TimeTest {

    @Test
    void roundToPrecision() {
        var localTime = LocalTime.parse("10:30:45");
        var time = new Time(localTime, Precision.MINUTE);

        var truncatedTime = (Time) time.roundToPrecision(Precision.MILLISECOND, false);
        assertEquals("10:30", truncatedTime.getTime().toString());
        assertEquals(Precision.MINUTE, truncatedTime.getPrecision());

        truncatedTime = (Time) time.roundToPrecision(Precision.MINUTE, false);
        assertEquals("10:30", truncatedTime.getTime().toString());
        assertEquals(Precision.MINUTE, truncatedTime.getPrecision());

        truncatedTime = (Time) time.roundToPrecision(Precision.MINUTE, true);
        assertEquals("10:30", truncatedTime.getTime().toString());
        assertEquals(Precision.MINUTE, truncatedTime.getPrecision());

        truncatedTime = (Time) time.roundToPrecision(Precision.HOUR, false);
        assertEquals("10:00", truncatedTime.getTime().toString());
        assertEquals(Precision.HOUR, truncatedTime.getPrecision());

        truncatedTime = (Time) time.roundToPrecision(Precision.HOUR, true);
        assertEquals("11:00", truncatedTime.getTime().toString());
        assertEquals(Precision.HOUR, truncatedTime.getPrecision());

        truncatedTime = (Time) time.roundToPrecision(Precision.MONTH, false);
        assertNull(truncatedTime);
    }
}
