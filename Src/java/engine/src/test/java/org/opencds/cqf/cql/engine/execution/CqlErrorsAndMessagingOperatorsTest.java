package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.SystemDataProvider;

import org.opencds.cqf.cql.engine.elm.visiting.obfuscate.PHIObfuscator;
import org.opencds.cqf.cql.engine.elm.visiting.obfuscate.RedactingPHIObfuscator;
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
        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlErrorsAndMessagingOperatorsTest"), set, null, null, null, null);

        Object result = result = evaluationResult.expressionResults.get("TestMessageInfo").value();
        assertThat(result, is(1));
        //Assert.assertEquals(result.toString(), "100: Test Message");

        result = result = evaluationResult.expressionResults.get("TestMessageWarn").value();
        assertThat(result, is(2));
        //Assert.assertEquals(result.toString(), "200: You have been warned!");

        result = result = evaluationResult.expressionResults.get("TestMessageTrace").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList(3, 4, 5))));
        //Assert.assertEquals(result.toString(), "300: This is a trace\n[3, 4, 5]");


        result = result = evaluationResult.expressionResults.get("TestMessageWithNullSeverity").value();
        assertThat(result, is(1));

        result = result = evaluationResult.expressionResults.get("TestMessageWithNullSource").value();
        assertThat(result == null, is(true));

        result = result = evaluationResult.expressionResults.get("TestMessageWithNullCondition").value();
        assertThat(result, is(1));

        result = result = evaluationResult.expressionResults.get("TestMessageWithNullCode").value();
        assertThat(result, is(1));

        result = result = evaluationResult.expressionResults.get("TestMessageWithNullMessage").value();
        assertThat(result, is(1));

        result = result = evaluationResult.expressionResults.get("TestWarningWithNullSource").value();
        assertThat(result == null, is(true));

        result = result = evaluationResult.expressionResults.get("TestWarningWithNullCondition").value();
        assertThat(result, is(1));

        result = result = evaluationResult.expressionResults.get("TestWarningWithNullCode").value();
        assertThat(result, is(1));

        result = result = evaluationResult.expressionResults.get("TestWarningWithNullMessage").value();
        assertThat(result, is(1));

        result = result = evaluationResult.expressionResults.get("TestTraceWithNullSource").value();
        assertThat(result == null, is(true));

        result = result = evaluationResult.expressionResults.get("TestTraceWithNullCondition").value();
        assertThat(result, is(1));

        result = result = evaluationResult.expressionResults.get("TestTraceWithNullCode").value();
        assertThat(result, is(1));

        result = result = evaluationResult.expressionResults.get("TestTraceWithNullMessage").value();
        assertThat(result, is(1));

    }

    @Test
    public void TestObfuscation() {
        Map<String, DataProvider> dataProviders = new HashMap<>();
        dataProviders.put("urn:hl7-org:elm-types:r1", new CustomSystemDataProvider());
        Environment environment = new Environment(getLibraryManager());
        environment.setDataProviders(dataProviders);

        CqlEngineVisitor visitor = new CqlEngineVisitor(environment, null, null, null, createOptionsMin());


        try {
           Object evaluationResult = visitor.evaluate(toElmIdentifier("CqlErrorsAndMessagingOperatorsTest"), Set.of("TestMessageObfuscation"), null, null, null, null);
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
