package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity

class ExpandEvaluatorTest {
    @Test
    fun testExpand() {
        val interval = Interval(1, true, 5, true)
        val per = Quantity().withUnit("1").withValue(BigDecimal.ONE)
        val result = ExpandEvaluator.expand(interval, per, null)
        val value = Assertions.assertInstanceOf(java.util.ArrayList::class.java, result)
        Assertions.assertIterableEquals(value, listOf(1, 2, 3, 4, 5))
    }
}
