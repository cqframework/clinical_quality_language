package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList

class ExpandEvaluatorTest {
    @Test
    fun testExpand() {
        val interval = Interval(Integer.ONE, true, 5.toCqlInteger(), true)
        val per = Quantity().withUnit("1").withValue(BigDecimal("1"))
        val result = ExpandEvaluator.expand(interval, per, null)
        assertEquals(
            listOf(
                    Integer.ONE,
                    2.toCqlInteger(),
                    3.toCqlInteger(),
                    4.toCqlInteger(),
                    5.toCqlInteger(),
                )
                .toCqlList(),
            result,
        )
    }
}
