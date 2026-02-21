package org.opencds.cqf.cql.engine.execution

import java.time.ZoneId
import java.time.ZonedDateTime
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.AfterEvaluator
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval

internal class CqlDateTimeOperatorsTest : CqlTestBase() {
    /** [org.opencds.cqf.cql.engine.elm.execution.AfterEvaluator.evaluate] */
    @Test
    fun after() {
        Assertions.assertThrows(CqlException::class.java) {
            AfterEvaluator.after(12, "This is an error", null, engine.state)
        }
    }

    /** [org.opencds.cqf.cql.engine.elm.execution.DurationBetweenEvaluator.evaluate] */
    @Test
    fun duration() {
        var value = engine.expression(library, "DateTimeDurationBetweenYear")
        MatcherAssert.assertThat(value, Matchers.`is`(5))

        value = engine.expression(library, "DateTimeDurationBetweenUncertainInterval")
        Assertions.assertEquals(17, (value as Interval).start)
        Assertions.assertEquals(44, value.end)

        value = engine.expression(library, "DateTimeDurationBetweenUncertainInterval2")
        Assertions.assertEquals(5, (value as Interval).start)
        Assertions.assertEquals(16, value.end)

        //        assertThat(((Uncertainty)result).getUncertaintyInterval(), is(new Interval(5,
        // true, 17, true)));
        value = engine.expression(library, "DateTimeDurationBetweenUncertainAdd")
        Assertions.assertEquals(34, (value as Interval).start)
        Assertions.assertEquals(88, value.end)

        value = engine.expression(library, "DateTimeDurationBetweenUncertainSubtract")
        Assertions.assertEquals(12, (value as Interval).start)
        Assertions.assertEquals(28, value.end)

        value = engine.expression(library, "DateTimeDurationBetweenUncertainMultiply")
        Assertions.assertEquals(289, (value as Interval).start)
        Assertions.assertEquals(1936, value.end)

        value = engine.expression(library, "DurationInDaysA")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "DurationInDaysAA")
        MatcherAssert.assertThat(value, Matchers.`is`(1))
    }

    /** [org.opencds.cqf.cql.engine.elm.execution.NowEvaluator.evaluate] */
    @Test
    fun now() {
        val evaluationDateTime = DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 5, 4, 999)
        val value =
            engine.expression(library, "Issue34A", evaluationDateTime.dateTime!!.toZonedDateTime())
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, evaluationDateTime) == true)
        Assertions.assertEquals(
            (value as DateTime).dateTime!!.offset,
            evaluationDateTime.dateTime!!.offset,
        )
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
            Assertions.assertEquals(expected, value)
        }
    }

    companion object {
        private val library = VersionedIdentifier().withId("CqlDateTimeOperatorsTest")
    }
}
