package org.opencds.cqf.cql.engine.execution

import java.time.ZonedDateTime
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class CqlClinicalOperatorsTest : CqlTestBase() {
    @Test
    fun all_clinical_operators_tests() {
        val results =
            engine
                .evaluate {
                    library(toElmIdentifier("CqlClinicalOperatorsTest"))
                    evaluationDateTime =
                        ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, TimeZone.getDefault().toZoneId())
                }
                .onlyResultOrThrow

        var value = results["CalculateAgeYears"]!!.value
        assertEquals(6.toCqlInteger(), value)

        value = results["CalculateAgeMonths"]!!.value
        assertEquals(72.toCqlInteger(), value)

        value = results["CalculateAgeDays"]!!.value
        assertEquals(2191.toCqlInteger(), value)

        value = results["CalculateAgeHours"]!!.value
        assertEquals(52583.toCqlInteger(), value)

        value = results["CalculateAgeMinutes"]!!.value
        assertEquals(3155040.toCqlInteger(), value)

        value = results["CalculateAgeSeconds"]!!.value
        assertEquals(189302400.toCqlInteger(), value)

        value = results["CalculateAgeUncertain"]!!.value
        assertEquals(
            Interval(61.toCqlInteger(), true, 72.toCqlInteger(), true).toString(),
            value.toString(),
        )

        value = results["CalculateAgeAtYears"]!!.value
        assertEquals(17.toCqlInteger(), value)

        value = results["CalculateAgeAtMonths"]!!.value
        assertEquals(197.toCqlInteger(), value)

        value = results["CalculateAgeAtDays"]!!.value
        assertEquals(6038.toCqlInteger(), value)

        // BTR -> 2020-10-09
        // Was 144912, but that doesn't account for time zones.
        // Microsoft SQL Server also returns 144912, but this is a pretty absurd test case, changed
        // to 144911
        // After committing to the build site, the test fails because it happens to be running
        // somewhere that the
        // timezone behavior is different
        // So, changing this test to be a more reasonable test of hours calculation
        value = results["CalculateAgeAtHours"]!!.value
        assertEquals(27.toCqlInteger(), value)

        // BTR -> 2020-10-09
        // Was 8694720, same as SQL Server, but again, edge case, changing
        value = results["CalculateAgeAtMinutes"]!!.value
        assertEquals((27 * 60 + 10).toCqlInteger(), value)

        // BTR -> 2020-10-09
        // Was 521683200, same as SQL Server, but again, edge case, changing
        value = results["CalculateAgeAtSeconds"]!!.value
        assertEquals(((27 * 60 + 10) * 60 + 15).toCqlInteger(), value)

        value = results["CalculateAgeAtUncertain"]!!.value
        assertEquals(187.toCqlInteger(), (value as Interval).start)
        assertEquals(198.toCqlInteger(), value.end)

        value = results["Issue70A"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["Issue70B"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["CodeEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["CodeEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["CodeEqualNullVersion"]!!.value
        assertNull(value)

        value = results["ConceptEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ConceptEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ConceptEqualNullDisplay"]!!.value
        assertNull(value)

        value = results["CodeEqualNull"]!!.value
        assertNull(value)

        value = results["ConceptEqualNull"]!!.value
        assertNull(value)

        value = results["CodeEquivalentTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["CodeEquivalentFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ConceptEquivalentTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ConceptEquivalentTrueDisplayMismatch"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ConceptEquivalentTrueIntersection1And4"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ConceptEquivalentTrueIntersection2And4"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ConceptEquivalentFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["CodeEquivalentNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ConceptEquivalentNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["CodeToConceptEquivalentFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["CodeToConceptEquivalentTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ConceptToConceptMismatchedDisplayTrue"]!!.value
        assertEquals(Boolean.TRUE, value)
    }
}
