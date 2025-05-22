package org.opencds.cqf.cql.engine.elm.executing;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;

public class ExpandEvaluatorTest {

    @Test
    public void testExpand() {
        var interval = new Interval(1, true, 5, true);
        var per = new Quantity().withUnit("1").withValue(BigDecimal.ONE);
        var result = ExpandEvaluator.expand(interval, per, null);
        var value = assertInstanceOf(List.class, result);
        assertIterableEquals(value, List.of(1, 2, 3, 4, 5));
    }
}
