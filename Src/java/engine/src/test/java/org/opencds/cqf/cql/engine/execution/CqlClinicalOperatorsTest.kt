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
            engine.evaluate(
                toElmIdentifier("CqlClinicalOperatorsTest"),
                ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, TimeZone.getDefault().toZoneId()),
            )

        var value = results.forExpression("CalculateAgeYears").value()
        MatcherAssert.assertThat(value, Matchers.`is`(6))

        value = results.forExpression("CalculateAgeMonths").value()
        MatcherAssert.assertThat(value, Matchers.`is`(72))

        value = results.forExpression("CalculateAgeDays").value()
        MatcherAssert.assertThat(value, Matchers.`is`(2191))

        value = results.forExpression("CalculateAgeHours").value()
        MatcherAssert.assertThat(value, Matchers.`is`(52583))

        value = results.forExpression("CalculateAgeMinutes").value()
        MatcherAssert.assertThat(value, Matchers.`is`(3155040))

        value = results.forExpression("CalculateAgeSeconds").value()
        MatcherAssert.assertThat(value, Matchers.`is`(189302400))

        value = results.forExpression("CalculateAgeUncertain").value()
        MatcherAssert.assertThat(
            value.toString(),
            Matchers.`is`((Interval(61, true, 72, true)).toString()),
        )

        value = results.forExpression("CalculateAgeAtYears").value()
        MatcherAssert.assertThat(value, Matchers.`is`(17))

        value = results.forExpression("CalculateAgeAtMonths").value()
        MatcherAssert.assertThat(value, Matchers.`is`(197))

        value = results.forExpression("CalculateAgeAtDays").value()
        MatcherAssert.assertThat(value, Matchers.`is`(6038))

        // BTR -> 2020-10-09
        // Was 144912, but that doesn't account for time zones.
        // Microsoft SQL Server also returns 144912, but this is a pretty absurd test case, changed
        // to 144911
        // After committing to the build site, the test fails because it happens to be running
        // somewhere that the
        // timezone behavior is different
        // So, changing this test to be a more reasonable test of hours calculation
        value = results.forExpression("CalculateAgeAtHours").value()
        MatcherAssert.assertThat(value, Matchers.`is`(27))

        // BTR -> 2020-10-09
        // Was 8694720, same as SQL Server, but again, edge case, changing
        value = results.forExpression("CalculateAgeAtMinutes").value()
        MatcherAssert.assertThat(value, Matchers.`is`(27 * 60 + 10))

        // BTR -> 2020-10-09
        // Was 521683200, same as SQL Server, but again, edge case, changing
        value = results.forExpression("CalculateAgeAtSeconds").value()
        MatcherAssert.assertThat(value, Matchers.`is`((27 * 60 + 10) * 60 + 15))

        value = results.forExpression("CalculateAgeAtUncertain").value()
        Assertions.assertEquals(187, (value as Interval).start)
        Assertions.assertEquals(198, value.end)

        value = results.forExpression("Issue70A").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("Issue70B").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("CodeEqualTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("CodeEqualFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("CodeEqualNullVersion").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("ConceptEqualTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ConceptEqualFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ConceptEqualNullDisplay").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("CodeEqualNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("ConceptEqualNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("CodeEquivalentTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("CodeEquivalentFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ConceptEquivalentTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ConceptEquivalentTrueDisplayMismatch").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ConceptEquivalentTrueIntersection1And4").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ConceptEquivalentTrueIntersection2And4").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ConceptEquivalentFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("CodeEquivalentNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ConceptEquivalentNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("CodeToConceptEquivalentFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("CodeToConceptEquivalentTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ConceptToConceptMismatchedDisplayTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))
    }
}
