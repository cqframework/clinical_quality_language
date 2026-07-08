package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.cql2elm.CqlIncludeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.exception.CqlException

internal class CqlLibraryLoadingTest : CqlTestBase() {

    @Test
    fun missing_library_throws_error() {
        try {
            engine.evaluate { library("Not a library") }.onlyResultOrThrow
            Assertions.fail<Any?>()
        } catch (e: CqlIncludeException) {
            assertThat(e.message, Matchers.containsString("not load source"))
        }
    }

    @Test
    fun bad_library_throws_error() {
        try {
            engine.evaluate { library("CqlLibraryLoadingTest") }.onlyResultOrThrow
            Assertions.fail<Any?>()
        } catch (e: CqlException) {
            assertThat<String?>(e.message, Matchers.containsString("loaded, but had errors"))
        }
    }
}
