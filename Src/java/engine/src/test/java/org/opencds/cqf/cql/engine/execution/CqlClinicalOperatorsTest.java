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

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlClinicalOperatorsTest"), null, null, null, null, ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, TimeZone.getDefault().toZoneId()));


        Object result = evaluationResult.expressionResults.get("CalculateAgeYears").value();
        assertThat(result, is(6));

        result = evaluationResult.expressionResults.get("CalculateAgeMonths").value();
        assertThat(result, is(72));

        result = evaluationResult.expressionResults.get("CalculateAgeDays").value();
        assertThat(result, is(2191));

        result = evaluationResult.expressionResults.get("CalculateAgeHours").value();
        assertThat(result, is(52583));

        result = evaluationResult.expressionResults.get("CalculateAgeMinutes").value();
        assertThat(result, is(3155040));

        result = evaluationResult.expressionResults.get("CalculateAgeSeconds").value();
        assertThat(result, is(189302400));

        result = evaluationResult.expressionResults.get("CalculateAgeUncertain").value();
        assertThat(result.toString(), is((new Interval(61, true, 72, true)).toString()));

        result = evaluationResult.expressionResults.get("CalculateAgeAtYears").value();
        assertThat(result, is(17));

        result = evaluationResult.expressionResults.get("CalculateAgeAtMonths").value();
        assertThat(result, is(197));

        result = evaluationResult.expressionResults.get("CalculateAgeAtDays").value();
        assertThat(result, is(6038));

        // BTR -> 2020-10-09
        // Was 144912, but that doesn't account for time zones.
        // Microsoft SQL Server also returns 144912, but this is a pretty absurd test case, changed to 144911
        // After committing to the build site, the test fails because it happens to be running somewhere that the timezone behavior is different
        // So, changing this test to be a more reasonable test of hours calculation
        result = evaluationResult.expressionResults.get("CalculateAgeAtHours").value();
        assertThat(result, is(27));

        // BTR -> 2020-10-09
        // Was 8694720, same as SQL Server, but again, edge case, changing
        result = evaluationResult.expressionResults.get("CalculateAgeAtMinutes").value();
        assertThat(result, is(27 * 60 + 10));

        // BTR -> 2020-10-09
        // Was 521683200, same as SQL Server, but again, edge case, changing
        result = evaluationResult.expressionResults.get("CalculateAgeAtSeconds").value();
        assertThat(result, is((27 * 60 + 10) * 60 + 15));

        result = evaluationResult.expressionResults.get("CalculateAgeAtUncertain").value();
        Assert.assertTrue(((Interval)result).getStart().equals(187));
        Assert.assertTrue(((Interval)result).getEnd().equals(198));

        result = evaluationResult.expressionResults.get("Issue70A").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("Issue70B").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("CodeEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("CodeEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeEqualNullVersion").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("ConceptEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ConceptEqualNullDisplay").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("CodeEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("ConceptEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("CodeEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("CodeEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ConceptEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEquivalentTrueDisplayMismatch").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEquivalentTrueIntersection1And4").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEquivalentTrueIntersection2And4").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeEquivalentNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ConceptEquivalentNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeToConceptEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeToConceptEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptToConceptMismatchedDisplayTrue").value();
        assertThat(result, is(true));


    }
}
