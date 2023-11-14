package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlNulLogicalOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_null_logical_operators() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("CqlNullologicalOperatorsTest"));
        Object result;

        result = evaluationResult.forExpression("CoalesceANull").value();
        assertThat(result, is("a"));

        result = evaluationResult.forExpression("CoalesceNullA").value();
        assertThat(result, is("a"));

        result = evaluationResult.forExpression("CoalesceEmptyList").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("CoalesceListFirstA").value();
        assertThat(result, is("a"));

        result = evaluationResult.forExpression("CoalesceListLastA").value();
        assertThat(result, is("a"));

        result = evaluationResult.forExpression("CoalesceFirstList").value();
        assertThat(result, is(Collections.singletonList("a")));

        result = evaluationResult.forExpression("CoalesceLastList").value();
        assertThat(result, is(Collections.singletonList("a")));

        result = evaluationResult.forExpression("DateTimeCoalesce").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012, 5, 18)));

        result = evaluationResult.forExpression("DateTimeListCoalesce").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012, 5, 18)));

        result = evaluationResult.forExpression("TimeCoalesce").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 15, 33, 556)));

        result = evaluationResult.forExpression("TimeListCoalesce").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 15, 33, 556)));

        result = evaluationResult.forExpression("IsNullTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IsNullFalseEmptyString").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IsNullAlsoFalseAbcString").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IsNullAlsoFalseNumber1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IsNullAlsoFalseNumberZero").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IsFalseFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IsFalseTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IsFalseNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IsTrueTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IsTrueFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IsTrueNull").value();
        assertThat(result, is(false));

    }
}
