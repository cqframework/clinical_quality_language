package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import org.cqframework.cql.cql2elm.TestUtils.runSemanticTest
import org.junit.jupiter.api.Test

internal class AggregateTest {
    @Test
    @Throws(IOException::class)
    fun aggregate() {
        runSemanticTest("OperatorTests/Aggregate.cql", 0)
    }
}
