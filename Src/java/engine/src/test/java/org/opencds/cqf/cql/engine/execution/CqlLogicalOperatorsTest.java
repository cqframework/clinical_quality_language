package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlLogicalOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_logical_operators() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlLogicalOperatorsTest"));
        Object result;

        result = evaluationResult.expressionResults.get("TrueAndTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TrueAndFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TrueAndNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("FalseAndTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("FalseAndFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("FalseAndNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("NullAndTrue").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("NullAndFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("NullAndNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("NotTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("NotFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("NotNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TrueOrTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TrueOrFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TrueOrNull").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("FalseOrTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("FalseOrFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("FalseOrNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("NullOrTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("NullOrFalse").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("NullOrNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TrueXorTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TrueXorFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TrueXorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("FalseXorTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("FalseXorFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("FalseXorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("NullXorTrue").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("NullXorFalse").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("NullXorNull").value();
        assertThat(result, is(nullValue()));

    }
}
