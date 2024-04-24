package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SignatureOutputTests extends CqlTestBase {

    @Test
    void evaluate() {
        var results = engine.evaluate(toElmIdentifier("SignatureOutputTests"));
        var value = results.forExpression("TestIntegerOverload").value();
        assertThat(value, is(new BigDecimal("1")));

        value = results.forExpression("TestDecimalOverload").value();
        assertThat(value, is(new BigDecimal("1.0")));

        value = results.forExpression("TestMultipleOverload").value();
        assertThat(value, is(5));

        value = results.forExpression("TestIntegerMultipleOverload").value();
        assertThat(value, is(1));

        value = results.forExpression("TestDecimalMultipleOverload").value();
        assertThat(value, is(new BigDecimal("2.0")));

        value = results.forExpression("TestIntegerAndDecimalMultipleOverload").value();
        assertThat(value, is(1));
    }
}
