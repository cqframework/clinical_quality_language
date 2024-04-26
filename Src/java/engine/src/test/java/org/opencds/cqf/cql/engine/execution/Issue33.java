package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;

class Issue33 extends CqlTestBase {

    @Test
    void interval() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var results = engine.evaluate(toElmIdentifier("Issue33"));
        Object value = results.forExpression("Issue33").value();
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getStart(), new DateTime(bigDecimalZoneOffset, 2017, 12, 20, 11, 0, 0)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getEnd(), new DateTime(bigDecimalZoneOffset, 2017, 12, 20, 23, 59, 59, 999)));
    }
}
