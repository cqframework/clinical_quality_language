package org.cqframework.cql.cql2elm.operators;

import java.io.IOException;
import org.cqframework.cql.cql2elm.TestUtils;
import org.testng.annotations.Test;

public class AggregateTest {

    @Test
    public void testAggregate() throws IOException {
        TestUtils.runSemanticTest("OperatorTests/Aggregate.cql", 0);
    }
}
