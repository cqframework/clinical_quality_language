package org.opencds.cqf.cql.engine.runtime;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateTest {

    @Test
    void roundToHigherPrecision() {
        var localDate = LocalDate.parse("2025-07-01");
        var date = new Date(localDate, Precision.MONTH);

        var roundedDate = (Date) date.roundToPrecision(Precision.MILLISECOND, false);
        assertEquals("2025-07-01", roundedDate.getDate().toString());
        assertEquals(Precision.MONTH, roundedDate.getPrecision());
    }

    @Test
    void roundToSamePrecision() {
        var localDate = LocalDate.parse("2025-07-01");
        var date = new Date(localDate, Precision.MONTH);

        var roundedDate = (Date) date.roundToPrecision(Precision.MONTH, false);
        assertEquals("2025-07-01", roundedDate.getDate().toString());
        assertEquals(Precision.MONTH, roundedDate.getPrecision());

        roundedDate = (Date) date.roundToPrecision(Precision.MONTH, true);
        assertEquals("2025-07-01", roundedDate.getDate().toString());
        assertEquals(Precision.MONTH, roundedDate.getPrecision());
    }

    @Test
    void roundToLowerPrecision() {
        var localDate = LocalDate.parse("2025-07-01");
        var date = new Date(localDate, Precision.MONTH);

        var roundedDate = (Date) date.roundToPrecision(Precision.YEAR, false);
        assertEquals("2025-01-01", roundedDate.getDate().toString());
        assertEquals(Precision.YEAR, roundedDate.getPrecision());

        roundedDate = (Date) date.roundToPrecision(Precision.YEAR, true);
        assertEquals("2026-01-01", roundedDate.getDate().toString());
        assertEquals(Precision.YEAR, roundedDate.getPrecision());
    }
}
