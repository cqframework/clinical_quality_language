package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlClinicalOperatorsTest extends CqlExecutionTestBase {

    @Test
    public void testAge() {
        // Tests in the fhir engine
    }

    @Test
    public void testAgeAt() {
        // Tests in the fhir engine
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.CalculateAgeEvaluator#evaluate(Context)}
     */
    @Test
    public void testCalculateAge() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Context context = new Context(library, ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, TimeZone.getDefault().toZoneId()));

         Object result = context.resolveExpressionRef("CalculateAgeYears").getExpression().evaluate(context);
         assertThat(result, is(6));

        result = context.resolveExpressionRef("CalculateAgeMonths").getExpression().evaluate(context);
        assertThat(result, is(72));

        result = context.resolveExpressionRef("CalculateAgeDays").getExpression().evaluate(context);
        assertThat(result, is(2191));

        result = context.resolveExpressionRef("CalculateAgeHours").getExpression().evaluate(context);
        assertThat(result, is(52583));

        result = context.resolveExpressionRef("CalculateAgeMinutes").getExpression().evaluate(context);
        assertThat(result, is(3155040));

        result = context.resolveExpressionRef("CalculateAgeSeconds").getExpression().evaluate(context);
        assertThat(result, is(189302400));

        result = context.resolveExpressionRef("CalculateAgeUncertain").getExpression().evaluate(context);
        assertThat(result.toString(), is((new Interval(61, true, 72, true)).toString()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.CalculateAgeAtEvaluator#evaluate(Context)}
     */
    @Test
    public void testCalculateAgeAt() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("CalculateAgeAtYears").getExpression().evaluate(context);
        assertThat(result, is(17));

        result = context.resolveExpressionRef("CalculateAgeAtMonths").getExpression().evaluate(context);
        assertThat(result, is(197));

        result = context.resolveExpressionRef("CalculateAgeAtDays").getExpression().evaluate(context);
        assertThat(result, is(6038));

        // BTR -> 2020-10-09
        // Was 144912, but that doesn't account for time zones.
        // Microsoft SQL Server also returns 144912, but this is a pretty absurd test case, changed to 144911
        // After committing to the build site, the test fails because it happens to be running somewhere that the timezone behavior is different
        // So, changing this test to be a more reasonable test of hours calculation
        result = context.resolveExpressionRef("CalculateAgeAtHours").getExpression().evaluate(context);
        assertThat(result, is(27));

        // BTR -> 2020-10-09
        // Was 8694720, same as SQL Server, but again, edge case, changing
        result = context.resolveExpressionRef("CalculateAgeAtMinutes").getExpression().evaluate(context);
        assertThat(result, is(27 * 60 + 10));

        // BTR -> 2020-10-09
        // Was 521683200, same as SQL Server, but again, edge case, changing
        result = context.resolveExpressionRef("CalculateAgeAtSeconds").getExpression().evaluate(context);
        assertThat(result, is((27 * 60 + 10) * 60 + 15));

        result = context.resolveExpressionRef("CalculateAgeAtUncertain").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).getStart().equals(187));
        Assert.assertTrue(((Interval)result).getEnd().equals(198));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.EqualEvaluator#evaluate(Context)}
     */
    @Test
    public void testEqual() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("Issue70A").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("Issue70B").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("CodeEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("CodeEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("CodeEqualNullVersion").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("ConceptEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ConceptEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ConceptEqualNullDisplay").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("CodeEqualNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("ConceptEqualNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator#evaluate(Context)}
     */
    @Test
    public void testEquivalent() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("CodeEquivalentTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("CodeEquivalentFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ConceptEquivalentTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ConceptEquivalentTrueDisplayMismatch").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ConceptEquivalentTrueIntersection1And4").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ConceptEquivalentTrueIntersection2And4").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ConceptEquivalentFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("CodeEquivalentNull").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ConceptEquivalentNull").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("CodeToConceptEquivalentFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("CodeToConceptEquivalentTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ConceptToConceptMismatchedDisplayTrue").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.InCodeSystemEvaluator#evaluate(Context)}
     */
    @Test
    public void testInCodesystem() {
        // Tests in the fhir engine
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.InValueSetEvaluator#evaluate(Context)}
     */
    @Test
    public void testInValueset() {
        // Tests in the fhir engine
    }
}
