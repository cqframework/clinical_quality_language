package org.opencds.cqf.cql.engine.elm.executing;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.Quantity;

class PredecessorEvaluatorTest {

    @Test
    void predecessor() {
        assertEquals(
                new BigDecimal("19"),
                PredecessorEvaluator.predecessor(
                        new BigDecimal("20"), new Quantity().withValue(new BigDecimal("100"))));
        assertEquals(
                new BigDecimal("19.99"),
                PredecessorEvaluator.predecessor(
                        new BigDecimal("20"), new Quantity().withValue(new BigDecimal("100.00"))));

        var actualQuantity = (Quantity) PredecessorEvaluator.predecessor(
                new Quantity().withValue(new BigDecimal("20")).withUnit("g"),
                new Quantity().withValue(new BigDecimal("100.00")).withUnit("g"));
        assertEquals(new BigDecimal("19.99"), actualQuantity.getValue());
        assertEquals("g", actualQuantity.getUnit());
    }
}
