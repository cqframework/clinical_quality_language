package org.opencds.cqf.cql.engine.execution

import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.elm.executing.AfterEvaluator
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlDateTimeOperatorsTest : CqlTestBase() {
    /** [org.opencds.cqf.cql.engine.elm.execution.AfterEvaluator.evaluate] */
    @Test
    fun after() {
        assertFailsWith<CqlException> {
            AfterEvaluator.after(
                12.toCqlInteger(),
                "This is an error".toCqlString(),
                null,
                engine.state,
            )
        }
    }

    /** [org.opencds.cqf.cql.engine.elm.execution.DurationBetweenEvaluator.evaluate] */
    @Test
    fun duration() {
        var value = engine.expression(library, "DateTimeDurationBetweenYear")
        assertEquals(5.toCqlInteger(), value)

        value = engine.expression(library, "DateTimeDurationBetweenUncertainInterval")
        assertIs<Interval>(value)
        assertEquals(17.toCqlInteger(), value.start)
        assertEquals(44.toCqlInteger(), value.end)

        value = engine.expression(library, "DateTimeDurationBetweenUncertainInterval2")
        assertIs<Interval>(value)
        assertEquals(5.toCqlInteger(), value.start)
        assertEquals(16.toCqlInteger(), value.end)

        //        assertThat(((Uncertainty)result).getUncertaintyInterval(), is(new Interval(5,
        // true, 17, true)));
        value = engine.expression(library, "DateTimeDurationBetweenUncertainAdd")
        assertIs<Interval>(value)
        assertEquals(34.toCqlInteger(), value.start)
        assertEquals(88.toCqlInteger(), value.end)

        value = engine.expression(library, "DateTimeDurationBetweenUncertainSubtract")
        assertIs<Interval>(value)
        assertEquals(12.toCqlInteger(), value.start)
        assertEquals(28.toCqlInteger(), value.end)

        value = engine.expression(library, "DateTimeDurationBetweenUncertainMultiply")
        assertIs<Interval>(value)
        assertEquals(289.toCqlInteger(), value.start)
        assertEquals(1936.toCqlInteger(), value.end)

        value = engine.expression(library, "DurationInDaysA")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "DurationInDaysAA")
        assertEquals(Integer.ONE, value)
    }

    /** [org.opencds.cqf.cql.engine.elm.execution.NowEvaluator.evaluate] */
    @Test
    fun now() {
        val evaluationDateTime = DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 5, 4, 999)
        val value =
            engine.expression(library, "Issue34A", evaluationDateTime.dateTime!!.toZonedDateTime())
        assertTrue(EquivalentEvaluator.equivalent(value, evaluationDateTime).value == true)
        assertEquals((value as DateTime).dateTime!!.offset, evaluationDateTime.dateTime!!.offset)
    }

    /** [org.opencds.cqf.cql.engine.elm.execution.TimeOfDayEvaluator.evaluate] */
    @Test
    fun timeOfDay() {
        // TODO: uncomment once Time(x,x,x,x,x) format is fixed
        // Context context = new Context(library);
        // var value = engine.expression(library, "TimeOfDayTest").value;
        // assertThat(((Time)result).getPartial().getValue(0), is(10));
    }

    /** [org.opencds.cqf.cql.engine.elm.execution.TodayEvaluator.evaluate] */
    @Test
    fun today() {
        //        context = new Context(library, new DateTime(TemporalHelper.getDefaultOffset(),
        // 2016, 6, 10, 5, 5, 4,
        // 999));
        //        value = engine.expression(library, "Issue34B").value;
        //        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(null,
        // 2016, 6, 10)));
        //        Assertions.assertTrue(((DateTime)
        // result).getDateTime().getOffset().equals(TemporalHelper.getDefaultZoneOffset()));
    }

    @Test
    fun defaultTimezoneOffset() {
        // Disable expression caching so that we can re-evaluate the same expression in different
        // time zones
        val engine = CqlEngine(environment!!, mutableSetOf())

        for (zoneId in
            mutableListOf<String?>("America/New_York", "Europe/London", "Pacific/Auckland")) {
            val evaluationZonedDateTime =
                ZonedDateTime.of(2024, 10, 3, 15, 54, 0, 0, ZoneId.of(zoneId))

            // Issue1420 calculates the difference in hours between `DateTime(y, m, d, h, m, s, ms)`
            // and
            // `DateTime(y, m, d, h, m, s, ms, 0)`, so it returns the timezone offset of the first
            // DateTime.
            // The first DateTime should have the timezone offset of the evaluation request as per
            // the spec.
            // CQL also has `timezoneoffset from DateTime(...)` but here we are testing a more
            // complex scenario.
            val value = engine.expression(library, "Issue1420", evaluationZonedDateTime)
            val expected = evaluationZonedDateTime.offset.totalSeconds / 3600
            assertEquals(expected.toCqlInteger(), value)
        }
    }

    companion object {
        private val library = VersionedIdentifier().withId("CqlDateTimeOperatorsTest")
    }
}
