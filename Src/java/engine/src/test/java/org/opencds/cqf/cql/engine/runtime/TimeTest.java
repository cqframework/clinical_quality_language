package org.opencds.cqf.cql.engine.runtime;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class TimeTest {

    @Test
    void roundToHigherPrecision() {
        var localTime = LocalTime.parse("10:30:45");
        var time = new Time(localTime, Precision.MINUTE);

        var roundedTime = (Time) time.roundToPrecision(Precision.MILLISECOND, false);
        assertEquals("10:30", roundedTime.getTime().toString());
        assertEquals(Precision.MINUTE, roundedTime.getPrecision());
    }

    @Test
    void roundToSamePrecision() {
        var localTime = LocalTime.parse("10:30:45");
        var time = new Time(localTime, Precision.MINUTE);

        var roundedTime = (Time) time.roundToPrecision(Precision.MINUTE, false);
        assertEquals("10:30", roundedTime.getTime().toString());
        assertEquals(Precision.MINUTE, roundedTime.getPrecision());
    }

    @Test
    void roundToLowerPrecision() {
        var localTime = LocalTime.parse("10:30:45");
        var time = new Time(localTime, Precision.MINUTE);

        var roundedTime = (Time) time.roundToPrecision(Precision.HOUR, false);
        assertEquals("10:00", roundedTime.getTime().toString());
        assertEquals(Precision.HOUR, roundedTime.getPrecision());

        roundedTime = (Time) time.roundToPrecision(Precision.HOUR, true);
        assertEquals("11:00", roundedTime.getTime().toString());
        assertEquals(Precision.HOUR, roundedTime.getPrecision());

        roundedTime = (Time) time.roundToPrecision(Precision.MONTH, false);
        assertNull(roundedTime);
    }
}
