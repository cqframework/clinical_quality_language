package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class Issue39 extends CqlTestBase {

    @Test
    void interval() {
        var results = engine.evaluate(toElmIdentifier("Issue39"));
        Object value = results.forExpression("EquivalentIntervals").value();
        assertThat(value, is(true));
    }
}
