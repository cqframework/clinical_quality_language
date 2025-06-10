package org.opencds.cqf.cql.engine.runtime;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class TemporalHelperTest {

    @Test
    void truncateOffsetDateTimeToPrecision() {
        var offsetDateTime = OffsetDateTime.parse("2025-07-15T10:30:45.123-04:30");
        assertEquals(
                "2025-07-15T10:30:45.123-04:30",
                TemporalHelper.truncateToPrecision(offsetDateTime, Precision.MILLISECOND)
                        .toString());
        assertEquals(
                "2025-07-15T10:30:45-04:30",
                TemporalHelper.truncateToPrecision(offsetDateTime, Precision.SECOND)
                        .toString());
        assertEquals(
                "2025-07-15T10:30-04:30",
                TemporalHelper.truncateToPrecision(offsetDateTime, Precision.MINUTE)
                        .toString());
        assertEquals(
                "2025-07-15T10:00-04:30",
                TemporalHelper.truncateToPrecision(offsetDateTime, Precision.HOUR)
                        .toString());
        assertEquals(
                "2025-07-15T00:00-04:30",
                TemporalHelper.truncateToPrecision(offsetDateTime, Precision.DAY)
                        .toString());
        assertEquals(
                "2025-07-15T00:00-04:30",
                TemporalHelper.truncateToPrecision(offsetDateTime, Precision.WEEK)
                        .toString());
        assertEquals(
                "2025-07-01T00:00-04:30",
                TemporalHelper.truncateToPrecision(offsetDateTime, Precision.MONTH)
                        .toString());
        assertEquals(
                "2025-01-01T00:00-04:30",
                TemporalHelper.truncateToPrecision(offsetDateTime, Precision.YEAR)
                        .toString());
    }

    @Test
    void truncateLocalDateToPrecision() {
        var localDate = LocalDate.parse("2025-07-15");
        assertEquals(
                "2025-07-15",
                TemporalHelper.truncateToPrecision(localDate, Precision.MILLISECOND)
                        .toString());
        assertEquals(
                "2025-07-15",
                TemporalHelper.truncateToPrecision(localDate, Precision.SECOND).toString());
        assertEquals(
                "2025-07-15",
                TemporalHelper.truncateToPrecision(localDate, Precision.MINUTE).toString());
        assertEquals(
                "2025-07-15",
                TemporalHelper.truncateToPrecision(localDate, Precision.HOUR).toString());
        assertEquals(
                "2025-07-15",
                TemporalHelper.truncateToPrecision(localDate, Precision.DAY).toString());
        assertEquals(
                "2025-07-15",
                TemporalHelper.truncateToPrecision(localDate, Precision.WEEK).toString());
        assertEquals(
                "2025-07-01",
                TemporalHelper.truncateToPrecision(localDate, Precision.MONTH).toString());
        assertEquals(
                "2025-01-01",
                TemporalHelper.truncateToPrecision(localDate, Precision.YEAR).toString());
    }
}
