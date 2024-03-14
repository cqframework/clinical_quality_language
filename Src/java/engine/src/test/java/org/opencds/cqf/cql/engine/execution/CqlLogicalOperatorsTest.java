package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.testng.annotations.Test;

public class CqlLogicalOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_logical_operators() {
        var results = engine.evaluate(toElmIdentifier("CqlLogicalOperatorsTest"));
        var value = results.forExpression("TrueAndTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TrueAndFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TrueAndNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("FalseAndTrue").value();
        assertThat(value, is(false));

        value = results.forExpression("FalseAndFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("FalseAndNull").value();
        assertThat(value, is(false));

        value = results.forExpression("NullAndTrue").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("NullAndFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("NullAndNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("NotTrue").value();
        assertThat(value, is(false));

        value = results.forExpression("NotFalse").value();
        assertThat(value, is(true));

        value = results.forExpression("NotNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TrueOrTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TrueOrFalse").value();
        assertThat(value, is(true));

        value = results.forExpression("TrueOrNull").value();
        assertThat(value, is(true));

        value = results.forExpression("FalseOrTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("FalseOrFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("FalseOrNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("NullOrTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("NullOrFalse").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("NullOrNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TrueXorTrue").value();
        assertThat(value, is(false));

        value = results.forExpression("TrueXorFalse").value();
        assertThat(value, is(true));

        value = results.forExpression("TrueXorNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("FalseXorTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("FalseXorFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("FalseXorNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("NullXorTrue").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("NullXorFalse").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("NullXorNull").value();
        assertThat(value, is(nullValue()));
    }
}
