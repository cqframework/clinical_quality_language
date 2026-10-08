package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

class PredecessorEvaluatorTest {
    @Test
    fun predecessor() {
        assertEquals(
            BigDecimal("19").toCqlDecimal(),
            PredecessorEvaluator.predecessor(
                BigDecimal("20").toCqlDecimal(),
                Quantity().withValue(BigDecimal("100")),
            ),
        )
        assertEquals(
            BigDecimal("19.99").toCqlDecimal(),
            PredecessorEvaluator.predecessor(
                BigDecimal("20").toCqlDecimal(),
                Quantity().withValue(BigDecimal("100.00")),
            ),
        )

        val actualQuantity =
            PredecessorEvaluator.predecessor(
                Quantity().withValue(BigDecimal("20")).withUnit("g"),
                Quantity().withValue(BigDecimal("100.00")).withUnit("g"),
            ) as Quantity
        assertEquals(BigDecimal("19.99"), actualQuantity.value)
        assertEquals("g", actualQuantity.unit)
    }
}
