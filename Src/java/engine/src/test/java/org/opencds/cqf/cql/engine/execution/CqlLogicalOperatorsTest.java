package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.*;
import org.testng.annotations.Test;

public class CqlLogicalOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_logical_operators() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toIdentifier("CqlLogicalOperatorsTest"));
        Object result;

        result = evaluationResult.forExpression("TrueAndTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TrueAndFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TrueAndNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("FalseAndTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("FalseAndFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("FalseAndNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("NullAndTrue").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("NullAndFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("NullAndNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("NotTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("NotFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("NotNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TrueOrTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TrueOrFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TrueOrNull").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("FalseOrTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("FalseOrFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("FalseOrNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("NullOrTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("NullOrFalse").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("NullOrNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TrueXorTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TrueXorFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TrueXorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("FalseXorTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("FalseXorFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("FalseXorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("NullXorTrue").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("NullXorFalse").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("NullXorNull").value();
        assertThat(result, is(nullValue()));
    }
}
