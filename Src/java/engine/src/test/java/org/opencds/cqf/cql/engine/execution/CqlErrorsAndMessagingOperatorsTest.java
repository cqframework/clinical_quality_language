package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.SystemDataProvider;

import org.opencds.cqf.cql.engine.elm.executing.obfuscate.PHIObfuscator;
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.RedactingPHIObfuscator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("removal")
public class CqlErrorsAndMessagingOperatorsTest extends CqlTestBase {

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlErrorsAndMessagingOperatorsTest");

    @Test
    public void test_message() {
        Object result = engine.expression(library, "TestMessageInfo").value();
        assertThat(result, is(1));
        //Assert.assertEquals(result.toString(), "100: Test Message");

        result = engine.expression(library, "TestMessageWarn").value();
        assertThat(result, is(2));
        //Assert.assertEquals(result.toString(), "200: You have been warned!");

        result = engine.expression(library, "TestMessageTrace").value();
        assertThat(result, is(new ArrayList<Object>(Arrays.asList(3, 4, 5))));
        //Assert.assertEquals(result.toString(), "300: This is a trace\n[3, 4, 5]");

        try {
            result = engine.expression(library, "TestMessageError").value();
        } catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("400: This is an error!%n"));
        }

        result = engine.expression(library, "TestMessageWithNullSeverity").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TestMessageWithNullSource").value();
        assertThat(result == null, is(true));

        result = engine.expression(library, "TestMessageWithNullCondition").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TestMessageWithNullCode").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TestMessageWithNullMessage").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TestWarningWithNullSource").value();
        assertThat(result == null, is(true));

        result = engine.expression(library, "TestWarningWithNullCondition").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TestWarningWithNullCode").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TestWarningWithNullMessage").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TestTraceWithNullSource").value();
        assertThat(result == null, is(true));

        result = engine.expression(library, "TestTraceWithNullCondition").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TestTraceWithNullCode").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TestTraceWithNullMessage").value();
        assertThat(result, is(1));

        try {
            result = engine.expression(library, "TestErrorWithNullSource").value();
            assertThat(result == null, is(true));
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("1: This is a message%nnull"));
        }

        try {
            result = engine.expression(library, "TestErrorWithNullCondition").value();
            assertThat(result, is(1));
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("1: This is a message%n"));
        }

        try {
            result = engine.expression(library, "TestErrorWithNullCode").value();
            assertThat(result, is(1));
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("This is a message%n"));
        }

        try {
            result = engine.expression(library, "TestErrorWithNullMessage").value();
            assertThat(result, is(1));
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("1: null%n"));
        }
    }

    @Test
    public void test_obfuscation() {
        Map<String, DataProvider> dataProviders = new HashMap<>();
        dataProviders.put("urn:hl7-org:elm-types:r1", new CustomSystemDataProvider());
        Environment environment = new Environment(getLibraryManager(), dataProviders, null);

        CqlEngine e = new CqlEngine(environment);
        try {
            e.expression(library, "TestMessageObfuscation").value();
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
