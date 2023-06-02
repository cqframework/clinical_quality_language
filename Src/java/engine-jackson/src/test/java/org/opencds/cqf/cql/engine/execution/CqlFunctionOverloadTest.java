package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.cqframework.cql.elm.execution.Library;
import org.fhir.ucum.UcumException;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlFunctionOverloadTest {

    TranslatorHelper translatorHelper;
    @BeforeSuite
    public void init() {
        translatorHelper = new TranslatorHelper();
    }

    @Test
    public void test_function_overload_with_no_signature() throws IOException, UcumException {
        Library library = translatorHelper.translate("FunctionOverloadTest.cql", LibraryBuilder.SignatureLevel.None);
        Context context = new Context(library);

        // TODO: reenable this once compile-time resolution works
        // try {
        //     context.resolveExpressionRef("TestAnyFunctionWithInteger").getExpression().evaluate(context);
        //     Assert.fail();
        // } catch (CqlException e) {
        //     assertThat(e.getMessage().startsWith("Signature not provided for overloaded function 'FunctionOverload.TestAny'"), is(true));
        // }

        var result = context.resolveExpressionRef("TestAnyFunctionWithInteger").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestAnyFunctionWithNoArgs").getExpression().evaluate(context);
        assertThat(result, is("any"));

        result = context.resolveExpressionRef("TestAnyFunctionWith2Args").getExpression().evaluate(context);
        assertThat(result, is(3));
    }

    @Test
    public void test_function_overload_with_signature() throws IOException, UcumException {
        Library library = translatorHelper.translate("FunctionOverloadTest.cql", LibraryBuilder.SignatureLevel.All);
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("TestAnyFunctionWithInteger").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestAnyFunctionWithString").getExpression().evaluate(context);
        assertThat(result, is("joe"));

        result = context.resolveExpressionRef("TestAnyFunctionWithDecimal").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("12.3")));

        result = context.resolveExpressionRef("TestAnyFunctionWithNoArgs").getExpression().evaluate(context);
        assertThat(result, is("any"));
    }

}
