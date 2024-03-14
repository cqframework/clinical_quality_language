package org.opencds.cqf.cql.engine.execution;

import static org.testng.Assert.assertEquals;

import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.annotations.Test;

public class Issue458 extends CqlTestBase {

    @Test
    public void testInterval() {
        var results = engine.evaluate(toElmIdentifier("Issue458"));
        var value = results.forExpression("Closed-Open Interval").value();
        Interval interval = (Interval) value;
        assertEquals(interval.toString(), "Interval[3, 5)");
    }
}
