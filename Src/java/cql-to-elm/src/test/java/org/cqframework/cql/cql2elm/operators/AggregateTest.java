package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.TestUtils;
import org.testng.annotations.Test;

import java.io.IOException;

public class AggregateTest {

    @Test
    public void testAggregate() throws IOException {
        TestUtils.runSemanticTest("OperatorTests/Aggregate.cql", 0);
    }
}
