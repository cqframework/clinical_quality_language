package org.opencds.cqf.cql.engine.runtime

import java.time.LocalDate
import java.time.OffsetDateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TemporalHelperTest {
    @Test
    fun truncateOffsetDateTimeToPrecision() {
        val offsetDateTime = OffsetDateTime.parse("2025-07-15T10:30:45.123-04:30")
        Assertions.assertEquals(
            "2025-07-15T10:30:45.123-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.MILLISECOND).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15T10:30:45-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.SECOND).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15T10:30-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.MINUTE).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15T10:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.HOUR).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15T00:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.DAY).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15T00:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.WEEK).toString(),
        )
        Assertions.assertEquals(
            "2025-07-01T00:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.MONTH).toString(),
        )
        Assertions.assertEquals(
            "2025-01-01T00:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.YEAR).toString(),
        )
    }

    @Test
    fun truncateLocalDateToPrecision() {
        val localDate = LocalDate.parse("2025-07-15")
        Assertions.assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.MILLISECOND).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.SECOND).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.MINUTE).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.HOUR).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.DAY).toString(),
        )
        Assertions.assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.WEEK).toString(),
        )
        Assertions.assertEquals(
            "2025-07-01",
            TemporalHelper.truncateToPrecision(localDate, Precision.MONTH).toString(),
        )
        Assertions.assertEquals(
            "2025-01-01",
            TemporalHelper.truncateToPrecision(localDate, Precision.YEAR).toString(),
        )
    }
}
