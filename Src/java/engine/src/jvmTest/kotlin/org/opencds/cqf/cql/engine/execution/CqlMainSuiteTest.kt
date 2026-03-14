package org.opencds.cqf.cql.engine.execution

import java.time.ZonedDateTime
import java.util.*
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerException.Companion.hasErrors
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CqlMainSuiteTest : CqlTestBase() {
    @Test
    fun cql_main_test_suite_compiles() {
        val errors = ArrayList<CqlCompilerException>()
        this.getLibrary(toElmIdentifier("CqlTestSuite"), errors, testCompilerOptions())
        Assertions.assertFalse(
            hasErrors(errors),
            String.format(
                "Test library compiled with the following errors : %s",
                this.toString(errors),
            ),
        )
    }

    @Test
    fun all_portable_cql_engine_tests() {
        val e = getEngine(testCompilerOptions())
        // TODO: It'd be interesting to be able to inspect the
        // possible set of expressions from the CQL engine API
        // prior to evaluating them all
        val result =
            e.evaluate {
                    library("CqlTestSuite")
                    evaluationDateTime = evalTime
                }
                .onlyResultOrThrow

        for (entry in result.expressionResults.entries) {
            if (entry.key.startsWith("test")) {
                if (entry.value.value != null) {
                    Assertions.assertEquals(
                        entry.value.value as String?,
                        entry.key.replace("test_".toRegex(), "") + " TEST PASSED",
                    )
                }
            }
        }
    }

    @Test
    fun cql_timezone_tests() {
        val e = getEngine(testCompilerOptions())

        // TODO: It'd be interesting to be able to inspect the
        // possible set of expressions from the CQL engine API
        // prior to evaluating them all
        val result =
            e.evaluate {
                    library("CqlTimeZoneTestSuite")
                    evaluationDateTime = evalTime
                }
                .onlyResultOrThrow

        for (entry in result.expressionResults.entries) {
            if (entry.key.startsWith("test")) {
                if (entry.value.value != null) {
                    Assertions.assertEquals(
                        entry.value.value as String?,
                        entry.key.replace("test_".toRegex(), "") + " TEST PASSED",
                    )
                }
            }
        }
    }

    private fun testCompilerOptions(): CqlCompilerOptions {
        val options = defaultOptions()
        // This test suite contains some definitions that use features that are usually
        // turned off for CQL.
        options.options.remove(CqlCompilerOptions.Options.DisableListDemotion)
        options.options.remove(CqlCompilerOptions.Options.DisableListPromotion)

        options.options.add(CqlCompilerOptions.Options.EnableResultTypes)

        // When called with the null argument, the toString function in the CqlTestSuite
        // library can only be unambiguously resolved at runtime if the library is
        // compiled with signature level set to Overloads or All.
        options.withSignatureLevel(LibraryBuilder.SignatureLevel.Overloads)

        return options
    }

    fun toString(errors: MutableList<CqlCompilerException>): String {
        val builder = StringBuilder()

        for (e in errors) {
            builder.append(e.toString() + System.lineSeparator())
            if (e.locator != null) {
                builder.append("at" + System.lineSeparator())
                builder.append(e.locator!!.toLocator() + System.lineSeparator())
            }
            builder.append(System.lineSeparator())
        }

        return builder.toString()
    }

    companion object {
        private val evalTime: ZonedDateTime =
            ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId())
    }
}
