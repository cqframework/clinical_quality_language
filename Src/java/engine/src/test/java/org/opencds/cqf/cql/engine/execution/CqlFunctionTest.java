package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.SignatureLevel;
import org.junit.jupiter.api.Test;

class CqlFunctionTest extends CqlTestBase {

    @Test
    void all_function_tests() {
        var compilerOptions =
                CqlCompilerOptions.defaultOptions().withSignatureLevel(SignatureLevel.Overloads);
        var engine = getEngine(compilerOptions);

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
