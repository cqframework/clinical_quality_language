package org.opencds.cqf.cql.engine.runtime;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ValueTest {

    @Test
    void getCoarsestScale() {
        assertEquals(0, Value.getCoarsestScale(Stream.of()));
        assertEquals(2, Value.getCoarsestScale(Stream.of(new BigDecimal("1.1234"), new BigDecimal("1.12"), null)));
    }

    @Test
    void roundToScale() {
        assertEquals(new BigDecimal("1.12"), Value.roundToScale(new BigDecimal("1.1234"), 2, false));
        assertEquals(new BigDecimal("1.13"), Value.roundToScale(new BigDecimal("1.1234"), 2, true));
        assertEquals(new BigDecimal("1.12"), Value.roundToScale(new BigDecimal("1.1200"), 2, false));
        assertEquals(new BigDecimal("1.12"), Value.roundToScale(new BigDecimal("1.1200"), 2, true));
        assertEquals(new BigDecimal("1.12"), Value.roundToScale(new BigDecimal("1.12"), 4, false));
        assertEquals(new BigDecimal("1.12"), Value.roundToScale(new BigDecimal("1.12"), 4, true));
    }
}
