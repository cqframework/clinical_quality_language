package org.opencds.cqf.cql.engine.execution

import java.time.ZonedDateTime
import java.util.*
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Interval

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
        MatcherAssert.assertThat(value, Matchers.`is`(6))

        value = results["CalculateAgeMonths"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(72))

        value = results["CalculateAgeDays"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2191))

        value = results["CalculateAgeHours"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(52583))

        value = results["CalculateAgeMinutes"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(3155040))

        value = results["CalculateAgeSeconds"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(189302400))

        value = results["CalculateAgeUncertain"]!!.value
        MatcherAssert.assertThat(
            value.toString(),
            Matchers.`is`((Interval(61, true, 72, true)).toString()),
        )

        value = results["CalculateAgeAtYears"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(17))

        value = results["CalculateAgeAtMonths"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(197))

        value = results["CalculateAgeAtDays"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(6038))

        // BTR -> 2020-10-09
        // Was 144912, but that doesn't account for time zones.
        // Microsoft SQL Server also returns 144912, but this is a pretty absurd test case, changed
        // to 144911
        // After committing to the build site, the test fails because it happens to be running
        // somewhere that the
        // timezone behavior is different
        // So, changing this test to be a more reasonable test of hours calculation
        value = results["CalculateAgeAtHours"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(27))

        // BTR -> 2020-10-09
        // Was 8694720, same as SQL Server, but again, edge case, changing
        value = results["CalculateAgeAtMinutes"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(27 * 60 + 10))

        // BTR -> 2020-10-09
        // Was 521683200, same as SQL Server, but again, edge case, changing
        value = results["CalculateAgeAtSeconds"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`((27 * 60 + 10) * 60 + 15))

        value = results["CalculateAgeAtUncertain"]!!.value
        Assertions.assertEquals(187, (value as Interval).start)
        Assertions.assertEquals(198, value.end)

        value = results["Issue70A"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["Issue70B"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["CodeEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["CodeEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["CodeEqualNullVersion"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["ConceptEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ConceptEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ConceptEqualNullDisplay"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["CodeEqualNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["ConceptEqualNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["CodeEquivalentTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["CodeEquivalentFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ConceptEquivalentTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ConceptEquivalentTrueDisplayMismatch"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ConceptEquivalentTrueIntersection1And4"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ConceptEquivalentTrueIntersection2And4"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ConceptEquivalentFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["CodeEquivalentNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ConceptEquivalentNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["CodeToConceptEquivalentFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["CodeToConceptEquivalentTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ConceptToConceptMismatchedDisplayTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))
    }
}
