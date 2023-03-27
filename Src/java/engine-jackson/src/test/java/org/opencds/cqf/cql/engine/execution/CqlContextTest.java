package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.Test;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlContextTest {

    @Test
    public void test_method_overload_with_no_signature() throws IOException {
        TranslatorHelper translatorHelper = new TranslatorHelper();
        Library library = translatorHelper.readJson(translatorHelper.readFromInputStream(CqlContextTest.class.getResourceAsStream("methodOverloadSingatureNone.json")));
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("TestAnyFunctionWithInteger").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestAnyFunctionWithString").getExpression().evaluate(context);
        assertThat(result, is("joe"));
    }

    @Test
    public void test_method_overload_with_signature() throws IOException {
        TranslatorHelper translatorHelper = new TranslatorHelper();
        Library library = translatorHelper.readJson(translatorHelper.readFromInputStream(CqlContextTest.class.getResourceAsStream("methodOverloadSingatureOverload.json")));
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("TestAnyFunctionWithInteger").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestAnyFunctionWithString").getExpression().evaluate(context);
        assertThat(result, is("joe"));
    }

}
