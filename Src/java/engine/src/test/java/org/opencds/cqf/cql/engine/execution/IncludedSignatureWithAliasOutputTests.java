package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import org.testng.annotations.Test;

public class IncludedSignatureWithAliasOutputTests extends CqlTestBase {

    @Test
    public void testEvaluate() {
        var results = engine.evaluate(toElmIdentifier("IncludedSignatureWithAliasOutputTests"));

        Object value = results.forExpression("TestOverload").value();
        assertThat(value, is(5));

        value = results.forExpression("TestOverloadOneInt").value();
        assertThat(value, is(1));

        value = results.forExpression("TestOverloadOneDecimal").value();
        assertThat(value, is(new BigDecimal("2.0")));

        value = results.forExpression("TestOverloadTwoInts").value();
        assertThat(value, is(1));

        value = results.forExpression("TestOverloadTwoDecimals").value();
        assertThat(value, is(new BigDecimal("2.0")));

        value = results.forExpression("TestOverloadOneIntOneDecimal").value();
        assertThat(value, is(new BigDecimal("2.0")));

        value = results.forExpression("TestOverloadOneIntTwoDecimal").value();
        assertThat(value, is(1));
    }
}
