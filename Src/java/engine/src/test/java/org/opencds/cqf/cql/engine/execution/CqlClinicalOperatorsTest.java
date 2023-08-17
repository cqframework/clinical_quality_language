package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlClinicalOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_clinical_operators_tests() {
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("CqlClinicalOperatorsTest"), ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, TimeZone.getDefault().toZoneId()));


        Object result = evaluationResult.forExpression("CalculateAgeYears").value();
        assertThat(result, is(6));

        result = evaluationResult.forExpression("CalculateAgeMonths").value();
        assertThat(result, is(72));

        result = evaluationResult.forExpression("CalculateAgeDays").value();
        assertThat(result, is(2191));

        result = evaluationResult.forExpression("CalculateAgeHours").value();
        assertThat(result, is(52583));

        result = evaluationResult.forExpression("CalculateAgeMinutes").value();
        assertThat(result, is(3155040));

        result = evaluationResult.forExpression("CalculateAgeSeconds").value();
        assertThat(result, is(189302400));

        result = evaluationResult.forExpression("CalculateAgeUncertain").value();
        assertThat(result.toString(), is((new Interval(61, true, 72, true)).toString()));

        result = evaluationResult.forExpression("CalculateAgeAtYears").value();
        assertThat(result, is(17));

        result = evaluationResult.forExpression("CalculateAgeAtMonths").value();
        assertThat(result, is(197));

        result = evaluationResult.forExpression("CalculateAgeAtDays").value();
        assertThat(result, is(6038));

        // BTR -> 2020-10-09
        // Was 144912, but that doesn't account for time zones.
        // Microsoft SQL Server also returns 144912, but this is a pretty absurd test case, changed to 144911
        // After committing to the build site, the test fails because it happens to be running somewhere that the timezone behavior is different
        // So, changing this test to be a more reasonable test of hours calculation
        result = evaluationResult.forExpression("CalculateAgeAtHours").value();
        assertThat(result, is(27));

        // BTR -> 2020-10-09
        // Was 8694720, same as SQL Server, but again, edge case, changing
        result = evaluationResult.forExpression("CalculateAgeAtMinutes").value();
        assertThat(result, is(27 * 60 + 10));

        // BTR -> 2020-10-09
        // Was 521683200, same as SQL Server, but again, edge case, changing
        result = evaluationResult.forExpression("CalculateAgeAtSeconds").value();
        assertThat(result, is((27 * 60 + 10) * 60 + 15));

        result = evaluationResult.forExpression("CalculateAgeAtUncertain").value();
        Assert.assertTrue(((Interval)result).getStart().equals(187));
        Assert.assertTrue(((Interval)result).getEnd().equals(198));

        result = evaluationResult.forExpression("Issue70A").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("Issue70B").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("CodeEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("CodeEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("CodeEqualNullVersion").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("ConceptEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ConceptEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ConceptEqualNullDisplay").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("CodeEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("ConceptEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("CodeEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("CodeEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ConceptEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ConceptEquivalentTrueDisplayMismatch").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ConceptEquivalentTrueIntersection1And4").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ConceptEquivalentTrueIntersection2And4").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ConceptEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("CodeEquivalentNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ConceptEquivalentNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("CodeToConceptEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("CodeToConceptEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ConceptToConceptMismatchedDisplayTrue").value();
        assertThat(result, is(true));


    }
}
