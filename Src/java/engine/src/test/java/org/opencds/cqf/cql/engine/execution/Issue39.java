package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.Test;

public class Issue39 extends CqlTestBase {

    @Test
    public void testInterval() {
        var results = engine.evaluate(toElmIdentifier("Issue39"));
        Object value = results.forExpression("EquivalentIntervals").value();
        assertThat(value, is(true));
    }
}
