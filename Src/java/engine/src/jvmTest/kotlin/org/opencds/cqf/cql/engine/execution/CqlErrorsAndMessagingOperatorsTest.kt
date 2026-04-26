package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.data.DataProvider
import org.opencds.cqf.cql.engine.data.SystemDataProvider
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.PHIObfuscator
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.RedactingPHIObfuscator
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList

internal class CqlErrorsAndMessagingOperatorsTest : CqlTestBase() {
    @Test
    fun message() {
        var value = engine.expression(library, "TestMessageInfo")
        assertEquals(Integer.ONE, value)

        // Assertions.assertEquals(result.toString(), "100: Test Message");
        value = engine.expression(library, "TestMessageWarn")
        assertEquals(2.toCqlInteger(), value)

        // Assertions.assertEquals(result.toString(), "200: You have been warned!");
        value = engine.expression(library, "TestMessageTrace")
        assertEquals(
            listOf(3.toCqlInteger(), 4.toCqlInteger(), 5.toCqlInteger()).toCqlList(),
            value,
        )

        // Assertions.assertEquals(result.toString(), "300: This is a trace\n[3, 4, 5]");
        try {
            value = engine.expression(library, "TestMessageError")
        } catch (re: RuntimeException) {
            assertEquals(re.message, String.format("400: This is an error!\n4"))
        }

        value = engine.expression(library, "TestMessageWithNullSeverity")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "TestMessageWithNullSource")
        assertNull(value)

        value = engine.expression(library, "TestMessageWithNullCondition")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "TestMessageWithNullCode")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "TestMessageWithNullMessage")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "TestWarningWithNullSource")
        assertNull(value)

        value = engine.expression(library, "TestWarningWithNullCondition")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "TestWarningWithNullCode")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "TestWarningWithNullMessage")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "TestTraceWithNullSource")
        assertNull(value)

        value = engine.expression(library, "TestTraceWithNullCondition")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "TestTraceWithNullCode")
        assertEquals(Integer.ONE, value)

        value = engine.expression(library, "TestTraceWithNullMessage")
        assertEquals(Integer.ONE, value)

        try {
            value = engine.expression(library, "TestErrorWithNullSource")
            assertNull(value)
        } catch (re: RuntimeException) {
            assertEquals(re.message, String.format("1: This is a message\nnull"))
        }

        try {
            value = engine.expression(library, "TestErrorWithNullCondition")
            assertEquals(Integer.ONE, value)
        } catch (re: RuntimeException) {
            assertEquals(re.message, String.format("1: This is a message\n"))
        }

        try {
            value = engine.expression(library, "TestErrorWithNullCode")
            assertEquals(Integer.ONE, value)
        } catch (re: RuntimeException) {
            assertEquals(re.message, String.format("This is a message\n1"))
        }

        try {
            value = engine.expression(library, "TestErrorWithNullMessage")
            assertEquals(Integer.ONE, value)
        } catch (re: RuntimeException) {
            assertEquals(re.message, String.format("1: null\n1"))
        }
    }

    @Test
    fun obfuscation() {
        val dataProviders: MutableMap<String?, DataProvider?> = HashMap()
        dataProviders["urn:hl7-org:elm-types:r1"] = CustomSystemDataProvider()
        val environment = Environment(libraryManager, dataProviders, null)

        val e = CqlEngine(environment)
        try {
            e.expression(library, "TestMessageObfuscation")
        } catch (result: RuntimeException) {
            assertEquals(
                String.format(
                    "400: This source should be redacted\n%s",
                    RedactingPHIObfuscator.REDACTED_MESSAGE,
                ),
                result.message,
            )
        }
    }

    private class CustomSystemDataProvider : SystemDataProvider() {
        override fun phiObfuscationSupplier(): () -> PHIObfuscator? {
            return { RedactingPHIObfuscator() }
        }
    }

    companion object {
        private val library = VersionedIdentifier().withId("CqlErrorsAndMessagingOperatorsTest")
    }
}
