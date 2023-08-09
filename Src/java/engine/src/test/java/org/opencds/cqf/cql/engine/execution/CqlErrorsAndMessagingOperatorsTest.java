package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.SystemDataProvider;

import org.opencds.cqf.cql.engine.elm.executing.obfuscate.PHIObfuscator;
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.RedactingPHIObfuscator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlErrorsAndMessagingOperatorsTest extends CqlTestBase {
    @Test
    public void test_all_errors_and_messaging_operators() throws IOException {

        EvaluationResult evaluationResult;
        Set<String> set = Set.of("TestMessageInfo","TestMessageWarn","TestMessageTrace","TestMessageWithNullSeverity","TestMessageWithNullSource",
               "TestMessageWithNullCondition","TestMessageWithNullCode","TestMessageWithNullMessage", "TestWarningWithNullSource",
                "TestWarningWithNullCondition","TestWarningWithNullCode","TestWarningWithNullMessage","TestTraceWithNullSource",
                "TestTraceWithNullCondition","TestTraceWithNullCode","TestTraceWithNullMessage");
        evaluationResult = engine.evaluate(toElmIdentifier("CqlErrorsAndMessagingOperatorsTest"), set);

        Object result = result = evaluationResult.forExpression("TestMessageInfo").value();
        assertThat(result, is(1));
        //Assert.assertEquals(result.toString(), "100: Test Message");

        result = result = evaluationResult.forExpression("TestMessageWarn").value();
        assertThat(result, is(2));
        //Assert.assertEquals(result.toString(), "200: You have been warned!");

        result = result = evaluationResult.forExpression("TestMessageTrace").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList(3, 4, 5))));
        //Assert.assertEquals(result.toString(), "300: This is a trace\n[3, 4, 5]");


        result = result = evaluationResult.forExpression("TestMessageWithNullSeverity").value();
        assertThat(result, is(1));

        result = result = evaluationResult.forExpression("TestMessageWithNullSource").value();
        assertThat(result == null, is(true));

        result = result = evaluationResult.forExpression("TestMessageWithNullCondition").value();
        assertThat(result, is(1));

        result = result = evaluationResult.forExpression("TestMessageWithNullCode").value();
        assertThat(result, is(1));

        result = result = evaluationResult.forExpression("TestMessageWithNullMessage").value();
        assertThat(result, is(1));

        result = result = evaluationResult.forExpression("TestWarningWithNullSource").value();
        assertThat(result == null, is(true));

        result = result = evaluationResult.forExpression("TestWarningWithNullCondition").value();
        assertThat(result, is(1));

        result = result = evaluationResult.forExpression("TestWarningWithNullCode").value();
        assertThat(result, is(1));

        result = result = evaluationResult.forExpression("TestWarningWithNullMessage").value();
        assertThat(result, is(1));

        result = result = evaluationResult.forExpression("TestTraceWithNullSource").value();
        assertThat(result == null, is(true));

        result = result = evaluationResult.forExpression("TestTraceWithNullCondition").value();
        assertThat(result, is(1));

        result = result = evaluationResult.forExpression("TestTraceWithNullCode").value();
        assertThat(result, is(1));

        result = result = evaluationResult.forExpression("TestTraceWithNullMessage").value();
        assertThat(result, is(1));

    }

    @Test
    public void TestObfuscation() {
        Map<String, DataProvider> dataProviders = new HashMap<>();
        dataProviders.put("urn:hl7-org:elm-types:r1", new CustomSystemDataProvider());
        Environment environment = new Environment(getLibraryManager(), dataProviders, null);

        CqlEngine visitor = new CqlEngine(environment);


        try {
           Object evaluationResult = visitor.evaluate(toElmIdentifier("CqlErrorsAndMessagingOperatorsTest"), Set.of("TestMessageObfuscation"));
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
