package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.Interval;

class Issue458 extends CqlTestBase {

    @Test
    void interval() {
        var results = engine.evaluate(toElmIdentifier("Issue458"));
        var value = results.forExpression("Closed-Open Interval").value();
        Interval interval = (Interval) value;
        assertEquals("Interval[3, 5)", interval.toString());
    }
}
