package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CqlFunctionOverloadTest extends CqlTestBase {

    @Test
    void function_overloads() {
        var results = engine.evaluate(toElmIdentifier("FunctionOverloadTest"));
        var value = results.forExpression("TestAnyFunctionWithInteger").value();
        assertThat(value, is(1));

        value = results.forExpression("TestAnyFunctionWithString").value();
        assertThat(value, is("joe"));

        value = results.forExpression("TestAnyFunctionWithDecimal").value();
        assertThat(value, is(new BigDecimal("12.3")));

        value = results.forExpression("TestAnyFunctionWithNoArgs").value();
        assertThat(value, is("any"));
    }
}
