package org.opencds.cqf.cql.engine.runtime

import java.math.BigDecimal
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ValueTest {
    @Test
    fun getCoarsestScale() {
        Assertions.assertEquals(0, Value.getCoarsestScale(Stream.of()))
        Assertions.assertEquals(
            2,
            Value.getCoarsestScale(Stream.of(BigDecimal("1.1234"), BigDecimal("1.12"), null)),
        )
    }

    @Test
    fun roundToScale() {
        Assertions.assertEquals(
            BigDecimal("1.12"),
            Value.roundToScale(BigDecimal("1.1234"), 2, false),
        )
        Assertions.assertEquals(
            BigDecimal("1.13"),
            Value.roundToScale(BigDecimal("1.1234"), 2, true),
        )
        Assertions.assertEquals(
            BigDecimal("1.12"),
            Value.roundToScale(BigDecimal("1.1200"), 2, false),
        )
        Assertions.assertEquals(
            BigDecimal("1.12"),
            Value.roundToScale(BigDecimal("1.1200"), 2, true),
        )
        Assertions.assertEquals(
            BigDecimal("1.12"),
            Value.roundToScale(BigDecimal("1.12"), 4, false),
        )
        Assertions.assertEquals(BigDecimal("1.12"), Value.roundToScale(BigDecimal("1.12"), 4, true))
    }
}
