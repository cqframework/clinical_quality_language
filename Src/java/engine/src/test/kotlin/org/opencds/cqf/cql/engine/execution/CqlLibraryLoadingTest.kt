package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.cql2elm.CqlIncludeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.exception.CqlException

internal class CqlLibraryLoadingTest : CqlTestBase() {
    @Test
    fun missing_identifier_throws_error() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            engine.evaluate(null as VersionedIdentifier?)
        }
    }

    @Test
    fun missing_library_throws_error() {
        try {
            engine.evaluate(VersionedIdentifier().withId("Not a library"))
            Assertions.fail<Any?>()
        } catch (e: CqlIncludeException) {
            assertThat(e.message, Matchers.containsString("not load source"))
        }
    }

    @Test
    fun bad_library_throws_error() {
        try {
            engine.evaluate(VersionedIdentifier().withId("CqlLibraryLoadingTest"))
            Assertions.fail<Any?>()
        } catch (e: CqlException) {
            assertThat<String?>(e.message, Matchers.containsString("loaded, but had errors"))
        }
    }
}
