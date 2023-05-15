package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.opencds.cqf.cql.engine.elm.visiting.EquivalentEvaluator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlNulLogicalOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_interval_operators() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlNullologicalOperatorsTest"), null, null, null, null, null);
        Object result;

        result = evaluationResult.expressionResults.get("CoalesceANull").value();
        assertThat(result, is("a"));

        result = evaluationResult.expressionResults.get("CoalesceNullA").value();
        assertThat(result, is("a"));

        result = evaluationResult.expressionResults.get("CoalesceEmptyList").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("CoalesceListFirstA").value();
        assertThat(result, is("a"));

        result = evaluationResult.expressionResults.get("CoalesceListLastA").value();
        assertThat(result, is("a"));

        result = evaluationResult.expressionResults.get("CoalesceFirstList").value();
        assertThat(result, is(Collections.singletonList("a")));

        result = evaluationResult.expressionResults.get("CoalesceLastList").value();
        assertThat(result, is(Collections.singletonList("a")));

        result = evaluationResult.expressionResults.get("DateTimeCoalesce").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 5, 18)));

        result = evaluationResult.expressionResults.get("DateTimeListCoalesce").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 5, 18)));

        result = evaluationResult.expressionResults.get("TimeCoalesce").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 15, 33, 556)));

        result = evaluationResult.expressionResults.get("TimeListCoalesce").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 15, 33, 556)));

        result = evaluationResult.expressionResults.get("IsNullTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IsNullFalseEmptyString").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IsNullAlsoFalseAbcString").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IsNullAlsoFalseNumber1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IsNullAlsoFalseNumberZero").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IsFalseFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IsFalseTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IsFalseNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IsTrueTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IsTrueFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IsTrueNull").value();
        assertThat(result, is(false));

    }
}
