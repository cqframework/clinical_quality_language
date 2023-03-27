package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlFunctionOverloadTest {

    @Test
    public void test_method_overload_with_no_signature() throws IOException {
        TranslatorHelper translatorHelper = new TranslatorHelper();
        Library library = translatorHelper.readLibraryFromInputJson(translatorHelper.readFromInputStream(
                CqlFunctionOverloadTest.class.getResourceAsStream("functionOverloadSignatureNone.json")));
        Context context = new Context(library);

        try {
            Object result = context.resolveExpressionRef("TestAnyFunctionWithInteger").getExpression().evaluate(context);
            Assert.fail();
        } catch (CqlException e) {
            System.out.println(e.getMessage());
            assertThat(e.getMessage().startsWith("Signature not provided for overloaded function 'TestAny'"), is(true));
        }
    }

    @Test
    public void test_method_overload_with_signature() throws IOException {
        TranslatorHelper translatorHelper = new TranslatorHelper();
        Library library = translatorHelper.readLibraryFromInputJson(translatorHelper.readFromInputStream(
                CqlFunctionOverloadTest.class.getResourceAsStream("functionOverloadSignatureOverload.json")));
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("TestAnyFunctionWithInteger").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestAnyFunctionWithString").getExpression().evaluate(context);
        assertThat(result, is("joe"));

        result = context.resolveExpressionRef("TestAnyFunctionWithDecimal").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("12.3")));
    }

}
