package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.time.ZonedDateTime;
import java.util.TimeZone;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlClinicalOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_clinical_operators_tests() {
        var results = engine.evaluate(
                toElmIdentifier("CqlClinicalOperatorsTest"),
                ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, TimeZone.getDefault().toZoneId()));

        var value = results.forExpression("CalculateAgeYears").value();
        assertThat(value, is(6));

        value = results.forExpression("CalculateAgeMonths").value();
        assertThat(value, is(72));

        value = results.forExpression("CalculateAgeDays").value();
        assertThat(value, is(2191));

        value = results.forExpression("CalculateAgeHours").value();
        assertThat(value, is(52583));

        value = results.forExpression("CalculateAgeMinutes").value();
        assertThat(value, is(3155040));

        value = results.forExpression("CalculateAgeSeconds").value();
        assertThat(value, is(189302400));

        value = results.forExpression("CalculateAgeUncertain").value();
        assertThat(value.toString(), is((new Interval(61, true, 72, true)).toString()));

        value = results.forExpression("CalculateAgeAtYears").value();
        assertThat(value, is(17));

        value = results.forExpression("CalculateAgeAtMonths").value();
        assertThat(value, is(197));

        value = results.forExpression("CalculateAgeAtDays").value();
        assertThat(value, is(6038));

        // BTR -> 2020-10-09
        // Was 144912, but that doesn't account for time zones.
        // Microsoft SQL Server also returns 144912, but this is a pretty absurd test case, changed to 144911
        // After committing to the build site, the test fails because it happens to be running somewhere that the
        // timezone behavior is different
        // So, changing this test to be a more reasonable test of hours calculation
        value = results.forExpression("CalculateAgeAtHours").value();
        assertThat(value, is(27));

        // BTR -> 2020-10-09
        // Was 8694720, same as SQL Server, but again, edge case, changing
        value = results.forExpression("CalculateAgeAtMinutes").value();
        assertThat(value, is(27 * 60 + 10));

        // BTR -> 2020-10-09
        // Was 521683200, same as SQL Server, but again, edge case, changing
        value = results.forExpression("CalculateAgeAtSeconds").value();
        assertThat(value, is((27 * 60 + 10) * 60 + 15));

        value = results.forExpression("CalculateAgeAtUncertain").value();
        Assert.assertTrue(((Interval) value).getStart().equals(187));
        Assert.assertTrue(((Interval) value).getEnd().equals(198));

        value = results.forExpression("Issue70A").value();
        assertThat(value, is(false));

        value = results.forExpression("Issue70B").value();
        assertThat(value, is(true));

        value = results.forExpression("CodeEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("CodeEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("CodeEqualNullVersion").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("ConceptEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ConceptEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ConceptEqualNullDisplay").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("CodeEqualNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("ConceptEqualNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("CodeEquivalentTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("CodeEquivalentFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ConceptEquivalentTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ConceptEquivalentTrueDisplayMismatch").value();
        assertThat(value, is(true));

        value = results.forExpression("ConceptEquivalentTrueIntersection1And4").value();
        assertThat(value, is(true));

        value = results.forExpression("ConceptEquivalentTrueIntersection2And4").value();
        assertThat(value, is(true));

        value = results.forExpression("ConceptEquivalentFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("CodeEquivalentNull").value();
        assertThat(value, is(false));

        value = results.forExpression("ConceptEquivalentNull").value();
        assertThat(value, is(false));

        value = results.forExpression("CodeToConceptEquivalentFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("CodeToConceptEquivalentTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ConceptToConceptMismatchedDisplayTrue").value();
        assertThat(value, is(true));
    }
}
