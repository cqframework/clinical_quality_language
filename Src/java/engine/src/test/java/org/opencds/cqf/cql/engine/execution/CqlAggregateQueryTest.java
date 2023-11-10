package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlAggregateQueryTest extends CqlTestBase {
    @Test
    void test_all_aggregate_clause_tests() {
        var evaluationResult = engine.evaluate(toElmIdentifier("CqlAggregateQueryTest"));
        var result = evaluationResult.forExpression("AggregateSumWithStart").value();
        assertThat(result, is(16));

        result = evaluationResult.forExpression("AggregateSumWithNull").value();
        assertThat(result, is(15));

        result = evaluationResult.forExpression("AggregateSumAll").value();
        assertThat(result, is(24));

        result = evaluationResult.forExpression("AggregateSumDistinct").value();
        assertThat(result, is(15));

        result = evaluationResult.forExpression("Multi").value();
        assertThat(result, is(6));

        result = evaluationResult.forExpression("MegaMulti").value();
        assertThat(result, is(36));

        result = evaluationResult.forExpression("MegaMultiDistinct").value();
        assertThat(result, is(37));
    }
}
