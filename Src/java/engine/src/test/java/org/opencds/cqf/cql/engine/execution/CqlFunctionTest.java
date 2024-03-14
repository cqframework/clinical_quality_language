package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.testng.annotations.Test;

public class CqlFunctionTest extends CqlTestBase {

    @Test
    public void test_all_function_tests() {
        var results = engine.evaluate(toElmIdentifier("CqlFunctionTests"));
        var value = results.forExpression("FunctionTestStringArg").value();
        assertThat(value, is("hello"));

        value = results.forExpression("FunctionTestNullStringArg").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("FunctionTestMultipleArgs").value();
        assertThat(value, is("hell0"));

        value = results.forExpression("FunctionTestNullMultipleArgs").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("FunctionTestOverload").value();
        assertThat(value, is("hell00.000"));

        value = results.forExpression("FunctionTestNullOverload").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("FunctionTestTupleArg").value();
        assertThat(value, is(3));

        value = results.forExpression("FunctionTestNullTupleArg").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("FunctionTestQuantityArg").value();
        assertThat(value, is("cm"));

        value = results.forExpression("FunctionTestNullQuantityArg").value();
        assertThat(value, is(nullValue()));
    }
}
