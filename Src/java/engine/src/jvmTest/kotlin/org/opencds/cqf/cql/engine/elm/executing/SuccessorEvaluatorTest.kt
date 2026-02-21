package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Quantity

internal class SuccessorEvaluatorTest {
    @Test
    fun successor() {
        Assertions.assertEquals(
            BigDecimal("21"),
            SuccessorEvaluator.successor(BigDecimal("20"), Quantity().withValue(BigDecimal("100"))),
        )
        Assertions.assertEquals(
            BigDecimal("20.01"),
            SuccessorEvaluator.successor(
                BigDecimal("20"),
                Quantity().withValue(BigDecimal("100.00")),
            ),
        )

        val actualQuantity =
            SuccessorEvaluator.successor(
                Quantity().withValue(BigDecimal("20")).withUnit("g"),
                Quantity().withValue(BigDecimal("100.00")).withUnit("g"),
            ) as Quantity
        Assertions.assertEquals(BigDecimal("20.01"), actualQuantity.value)
        Assertions.assertEquals("g", actualQuantity.unit)
    }
}
