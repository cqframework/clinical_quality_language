package org.opencds.cqf.cql.engine.runtime

import java.time.LocalTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TimeTest {
    @Test
    fun roundToHigherPrecision() {
        val localTime = LocalTime.parse("10:30:45")
        val time = Time(localTime, Precision.MINUTE)

        val roundedTime = time.roundToPrecision(Precision.MILLISECOND, false) as Time
        Assertions.assertEquals("10:30", roundedTime.time.toString())
        Assertions.assertEquals(Precision.MINUTE, roundedTime.precision)
    }

    @Test
    fun roundToSamePrecision() {
        val localTime = LocalTime.parse("10:30:45")
        val time = Time(localTime, Precision.MINUTE)

        val roundedTime = time.roundToPrecision(Precision.MINUTE, false) as Time
        Assertions.assertEquals("10:30", roundedTime.time.toString())
        Assertions.assertEquals(Precision.MINUTE, roundedTime.precision)
    }

    @Test
    fun roundToLowerPrecision() {
        val localTime = LocalTime.parse("10:30:45")
        val time = Time(localTime, Precision.MINUTE)

        var roundedTime = time.roundToPrecision(Precision.HOUR, false) as Time?
        Assertions.assertEquals("10:00", roundedTime!!.time.toString())
        Assertions.assertEquals(Precision.HOUR, roundedTime.precision)

        roundedTime = time.roundToPrecision(Precision.HOUR, true) as Time
        Assertions.assertEquals("11:00", roundedTime.time.toString())
        Assertions.assertEquals(Precision.HOUR, roundedTime.precision)

        roundedTime = time.roundToPrecision(Precision.MONTH, false) as Time?
        Assertions.assertNull(roundedTime)
    }
}
