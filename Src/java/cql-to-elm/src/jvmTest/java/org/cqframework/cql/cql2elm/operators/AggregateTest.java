package org.cqframework.cql.cql2elm.operators;

import java.io.IOException;
import org.cqframework.cql.cql2elm.TestUtils;
import org.junit.jupiter.api.Test;

class AggregateTest {

    @Test
    void aggregate() throws IOException {
        TestUtils.runSemanticTest("OperatorTests/Aggregate.cql", 0);
    }
}
