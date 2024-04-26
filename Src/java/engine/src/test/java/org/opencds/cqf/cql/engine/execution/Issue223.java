package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.junit.jupiter.api.Test;

class Issue223 extends CqlTestBase {

    @Test
    void interval() {
        var results = engine.evaluate(toElmIdentifier("Issue223"));
        var value = results.forExpression("Access Flattened List of List Items").value();
        List<?> list = (List<?>) value;
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is(true));

        value = results.forExpression("Access Flattened List of List Items in a Single Query")
                .value();
        list = (List<?>) value;
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is(true));
    }
}
