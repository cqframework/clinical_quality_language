package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Quantity

internal class PredecessorEvaluatorTest {
    @Test
    fun predecessor() {
        Assertions.assertEquals(
            BigDecimal("19"),
            PredecessorEvaluator.predecessor(
                BigDecimal("20"),
                Quantity().withValue(BigDecimal("100")),
            ),
        )
        Assertions.assertEquals(
            BigDecimal("19.99"),
            PredecessorEvaluator.predecessor(
                BigDecimal("20"),
                Quantity().withValue(BigDecimal("100.00")),
            ),
        )

        val actualQuantity =
            PredecessorEvaluator.predecessor(
                Quantity().withValue(BigDecimal("20")).withUnit("g"),
                Quantity().withValue(BigDecimal("100.00")).withUnit("g"),
            ) as Quantity
        Assertions.assertEquals(BigDecimal("19.99"), actualQuantity.value)
        Assertions.assertEquals("g", actualQuantity.unit)
    }
}
