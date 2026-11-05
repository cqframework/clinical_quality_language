package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

class SuccessorEvaluatorTest {
    @Test
    fun successor() {
        assertEquals(
            BigDecimal("21").toCqlDecimal(),
            SuccessorEvaluator.successor(
                BigDecimal("20").toCqlDecimal(),
                Quantity().withValue(BigDecimal("100")),
            ),
        )
        assertEquals(
            BigDecimal("20.01").toCqlDecimal(),
            SuccessorEvaluator.successor(
                BigDecimal("20").toCqlDecimal(),
                Quantity().withValue(BigDecimal("100.00")),
            ),
        )

        val actualQuantity =
            SuccessorEvaluator.successor(
                Quantity().withValue(BigDecimal("20")).withUnit("g"),
                Quantity().withValue(BigDecimal("100.00")).withUnit("g"),
            ) as Quantity
        assertEquals(BigDecimal("20.01"), actualQuantity.value)
        assertEquals("g", actualQuantity.unit)
    }
}
