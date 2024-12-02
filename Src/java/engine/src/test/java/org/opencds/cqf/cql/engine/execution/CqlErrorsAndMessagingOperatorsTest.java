package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Supplier;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.data.SystemDataProvider;
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.PHIObfuscator;
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.RedactingPHIObfuscator;

@SuppressWarnings("removal")
class CqlErrorsAndMessagingOperatorsTest extends CqlTestBase {

    private static final VersionedIdentifier library =
            new VersionedIdentifier().withId("CqlErrorsAndMessagingOperatorsTest");

    @Test
    void message() {
        engine.expression(library, "TestMessageInfo");
        assertEquals(getLastMessageFromEngine(), "100: Test Message");

        engine.expression(library, "TestMessageWarn");
        assertEquals(getLastMessageFromEngine(), "200: You have been warned!");

        engine.expression(library, "TestMessageTrace");
        assertEquals(getLastMessageFromEngine(), String.format("300: This is a trace%n"));

        var re = assertThrows(RuntimeException.class, () -> engine.expression(library, "TestMessageError"));
        assertEquals(re.getMessage(), String.format("400: This is an error!%n"));

        re = assertThrows(RuntimeException.class, () -> engine.expression(library, "TestErrorWithNullSource"));
        assertEquals(re.getMessage(), String.format("1: This is a message%nnull"));

        re = assertThrows(RuntimeException.class, () -> engine.expression(library, "TestErrorWithNullCode"));
        assertEquals(re.getMessage(), String.format("This is a message%n"));

        re = assertThrows(RuntimeException.class, () -> engine.expression(library, "TestErrorWithNullMessage"));
        assertEquals(re.getMessage(), String.format("1: null%n"));
    }

    @Test
    void obfuscation() {
        environment.registerDataProvider("urn:hl7-org:elm-types:r1", new CustomSystemDataProvider());

        var re = assertThrows(RuntimeException.class, () -> engine.expression(library, "TestMessageObfuscation"));
        assertEquals(
                re.getMessage(),
                String.format("400: This source should be redacted%n%s", RedactingPHIObfuscator.REDACTED_MESSAGE));
    }

    private static class CustomSystemDataProvider extends SystemDataProvider {
        @Override
        public Supplier<PHIObfuscator> phiObfuscationSupplier() {
            return RedactingPHIObfuscator::new;
        }
    }

    /**
     * @return The last message from the engine state.
     */
    String getLastMessageFromEngine() {
        var messages = engine.getState().getDebugResult().getMessages();
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1).getMessage();
    }
}
