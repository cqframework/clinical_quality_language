package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.testng.annotations.Test;

public class EmptyStringsTest extends CqlTestBase {

    @Test
    public void test_all_empty_string() {
        var results = engine.evaluate(toElmIdentifier("EmptyStringsTest"));
        var value = results.forExpression("Null").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("Space").value();
        assertThat(value, is(" "));

        value = results.forExpression("Empty").value();
        assertThat(value, is(""));
    }
}
