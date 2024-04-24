package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class DateComparatorTest extends CqlTestBase {

    @Test
    void date_comparator() {
        var results = engine.evaluate(toElmIdentifier("DateComparatorTest"));
        var value = results.forExpression("Date Comparator Test").value();
        assertThat(value, is(true));
    }
}
