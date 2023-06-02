package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import org.opencds.cqf.cql.engine.data.SystemDataProvider;
import org.opencds.cqf.cql.engine.elm.execution.obfuscate.PHIObfuscator;
import org.opencds.cqf.cql.engine.elm.execution.obfuscate.RedactingPHIObfuscator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlErrorsAndMessagingOperatorsTest extends CqlExecutionTestBase {
    @Test
    public void TestMessage() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("TestMessageInfo").evaluate(context);
        assertThat(result, is(1));
        //Assert.assertEquals(result.toString(), "100: Test Message");

        result = context.resolveExpressionRef("TestMessageWarn").evaluate(context);
        assertThat(result, is(2));
        //Assert.assertEquals(result.toString(), "200: You have been warned!");

        result = context.resolveExpressionRef("TestMessageTrace").evaluate(context);
        assertThat(result, is(new ArrayList<Object>(Arrays.asList(3, 4, 5))));
        //Assert.assertEquals(result.toString(), "300: This is a trace\n[3, 4, 5]");

        try {
            result = context.resolveExpressionRef("TestMessageError").evaluate(context);
        } catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("400: This is an error!%n"));
        }

        result = context.resolveExpressionRef("TestMessageWithNullSeverity").evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestMessageWithNullSource").evaluate(context);
        assertThat(result == null, is(true));

        result = context.resolveExpressionRef("TestMessageWithNullCondition").evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestMessageWithNullCode").evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestMessageWithNullMessage").evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestWarningWithNullSource").evaluate(context);
        assertThat(result == null, is(true));

        result = context.resolveExpressionRef("TestWarningWithNullCondition").evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestWarningWithNullCode").evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestWarningWithNullMessage").evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestTraceWithNullSource").evaluate(context);
        assertThat(result == null, is(true));

        result = context.resolveExpressionRef("TestTraceWithNullCondition").evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestTraceWithNullCode").evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestTraceWithNullMessage").evaluate(context);
        assertThat(result, is(1));

        try {
            result = context.resolveExpressionRef("TestErrorWithNullSource").evaluate(context);
            assertThat(result == null, is(true));
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("1: This is a message%nnull"));
        }

        try {
            result = context.resolveExpressionRef("TestErrorWithNullCondition").evaluate(context);
            assertThat(result, is(1));
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("1: This is a message%n"));
        }

        try {
            result = context.resolveExpressionRef("TestErrorWithNullCode").evaluate(context);
            assertThat(result, is(1));
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("This is a message%n"));
        }

        try {
            result = context.resolveExpressionRef("TestErrorWithNullMessage").evaluate(context);
            assertThat(result, is(1));
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("1: null%n"));
        }
    }

    @Test
    public void TestObfuscation() {
        Context context = new Context(library, new CustomSystemDataProvider());

        try {
            context.resolveExpressionRef("TestMessageObfuscation").evaluate(context);
        } catch (RuntimeException result) {
            Assert.assertEquals(result.getMessage(),
                                String.format("400: This source should be redacted%n%s",
                                              RedactingPHIObfuscator.REDACTED_MESSAGE));
        }
    }

    private static class CustomSystemDataProvider extends SystemDataProvider {
        @Override
        public Supplier<PHIObfuscator> phiObfuscationSupplier() {
            return RedactingPHIObfuscator::new;
        }
    }
}
