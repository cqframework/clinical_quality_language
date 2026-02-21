package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.DataProvider
import org.opencds.cqf.cql.engine.data.SystemDataProvider
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.PHIObfuscator
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.RedactingPHIObfuscator

internal class CqlErrorsAndMessagingOperatorsTest : CqlTestBase() {
    @Test
    fun message() {
        var value = engine.expression(library, "TestMessageInfo")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        // Assertions.assertEquals(result.toString(), "100: Test Message");
        value = engine.expression(library, "TestMessageWarn")
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        // Assertions.assertEquals(result.toString(), "200: You have been warned!");
        value = engine.expression(library, "TestMessageTrace")
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(ArrayList<Any?>(mutableListOf<Int?>(3, 4, 5))),
        )

        // Assertions.assertEquals(result.toString(), "300: This is a trace\n[3, 4, 5]");
        try {
            value = engine.expression(library, "TestMessageError")
        } catch (re: RuntimeException) {
            Assertions.assertEquals(re.message, String.format("400: This is an error!\n"))
        }

        value = engine.expression(library, "TestMessageWithNullSeverity")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "TestMessageWithNullSource")
        MatcherAssert.assertThat(value == null, Matchers.`is`(true))

        value = engine.expression(library, "TestMessageWithNullCondition")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "TestMessageWithNullCode")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "TestMessageWithNullMessage")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "TestWarningWithNullSource")
        MatcherAssert.assertThat(value == null, Matchers.`is`(true))

        value = engine.expression(library, "TestWarningWithNullCondition")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "TestWarningWithNullCode")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "TestWarningWithNullMessage")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "TestTraceWithNullSource")
        MatcherAssert.assertThat(value == null, Matchers.`is`(true))

        value = engine.expression(library, "TestTraceWithNullCondition")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "TestTraceWithNullCode")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = engine.expression(library, "TestTraceWithNullMessage")
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        try {
            value = engine.expression(library, "TestErrorWithNullSource")
            MatcherAssert.assertThat(value == null, Matchers.`is`(true))
        } catch (re: RuntimeException) {
            Assertions.assertEquals(re.message, String.format("1: This is a message\nnull"))
        }

        try {
            value = engine.expression(library, "TestErrorWithNullCondition")
            MatcherAssert.assertThat(value, Matchers.`is`(1))
        } catch (re: RuntimeException) {
            Assertions.assertEquals(re.message, String.format("1: This is a message\n"))
        }

        try {
            value = engine.expression(library, "TestErrorWithNullCode")
            MatcherAssert.assertThat(value, Matchers.`is`(1))
        } catch (re: RuntimeException) {
            Assertions.assertEquals(re.message, String.format("This is a message\n"))
        }

        try {
            value = engine.expression(library, "TestErrorWithNullMessage")
            MatcherAssert.assertThat(value, Matchers.`is`(1))
        } catch (re: RuntimeException) {
            Assertions.assertEquals(re.message, String.format("1: null\n"))
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
            Assertions.assertEquals(
                result.message,
                String.format(
                    "400: This source should be redacted\n%s",
                    RedactingPHIObfuscator.REDACTED_MESSAGE,
                ),
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
