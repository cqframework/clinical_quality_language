package org.opencds.cqf.cql.engine.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.util.localDateParse
import org.opencds.cqf.cql.engine.util.offsetDateTimeParse

internal class TemporalHelperTest {
    @Test
    fun truncateOffsetDateTimeToPrecision() {
        val offsetDateTime = offsetDateTimeParse("2025-07-15T10:30:45.123-04:30")
        assertEquals(
            "2025-07-15T10:30:45.123-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.MILLISECOND).toString(),
        )
        assertEquals(
            "2025-07-15T10:30:45-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.SECOND).toString(),
        )
        assertEquals(
            "2025-07-15T10:30-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.MINUTE).toString(),
        )
        assertEquals(
            "2025-07-15T10:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.HOUR).toString(),
        )
        assertEquals(
            "2025-07-15T00:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.DAY).toString(),
        )
        assertEquals(
            "2025-07-15T00:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.WEEK).toString(),
        )
        assertEquals(
            "2025-07-01T00:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.MONTH).toString(),
        )
        assertEquals(
            "2025-01-01T00:00-04:30",
            TemporalHelper.truncateToPrecision(offsetDateTime, Precision.YEAR).toString(),
        )
    }

    @Test
    fun truncateLocalDateToPrecision() {
        val localDate = localDateParse("2025-07-15")
        assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.MILLISECOND).toString(),
        )
        assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.SECOND).toString(),
        )
        assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.MINUTE).toString(),
        )
        assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.HOUR).toString(),
        )
        assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.DAY).toString(),
        )
        assertEquals(
            "2025-07-15",
            TemporalHelper.truncateToPrecision(localDate, Precision.WEEK).toString(),
        )
        assertEquals(
            "2025-07-01",
            TemporalHelper.truncateToPrecision(localDate, Precision.MONTH).toString(),
        )
        assertEquals(
            "2025-01-01",
            TemporalHelper.truncateToPrecision(localDate, Precision.YEAR).toString(),
        )
    }
}
