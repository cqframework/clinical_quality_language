package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.Test;

public class CqlAggregateQueryTest extends CqlTestBase {
    @Test
    void test_all_aggregate_clause_tests() {
        var results = engine.evaluate(toElmIdentifier("CqlAggregateQueryTest"));
        var value = results.forExpression("AggregateSumWithStart").value();
        assertThat(value, is(16));

        value = results.forExpression("AggregateSumWithNull").value();
        assertThat(value, is(15));

        value = results.forExpression("AggregateSumAll").value();
        assertThat(value, is(24));

        value = results.forExpression("AggregateSumDistinct").value();
        assertThat(value, is(15));

        value = results.forExpression("Multi").value();
        assertThat(value, is(6));

        value = results.forExpression("MegaMulti").value();
        assertThat(value, is(36));

        value = results.forExpression("MegaMultiDistinct").value();
        assertThat(value, is(37));
    }
}
