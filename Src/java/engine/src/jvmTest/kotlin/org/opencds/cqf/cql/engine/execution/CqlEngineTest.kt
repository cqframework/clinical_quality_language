package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.opencds.cqf.cql.engine.debug.DebugMap
import org.opencds.cqf.cql.engine.debug.DebugResultEntry
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.List

internal class CqlEngineTest : CqlTestBase() {
    @Test
    fun debugMap() {
        // The specific library isn't important, just that it has a debug map

        val debugMap = DebugMap()
        debugMap.isLoggingEnabled = true
        val results =
            engine
                .evaluate {
                    library("CqlEngineTest")
                    this@evaluate.debugMap = debugMap
                }
                .onlyResultOrThrow

        val libraryDebug = results.debugResult!!.libraryResults["CqlEngineTest"]!!.results

        assertNotNull(libraryDebug)

        // Find the debug result for the AnInteger expression
        // It's indexed by location
        val result =
            libraryDebug.keys.stream().filter { e -> e!!.locator == "6:1-6:21" }.findFirst()

        assertTrue(result.isPresent)

        val debugResult: MutableList<DebugResultEntry?> = libraryDebug[result.get()]!!
        assertEquals(1, debugResult.size)
    }

    @Test
    fun invalidCql() {
        val versionedIdentifier = toElmIdentifier("Invalid")
        val exception =
            assertFailsWith<CqlException> {
                engine.evaluate { library(versionedIdentifier) }.onlyResultOrThrow
            }
        assertContains(exception.message!!, "Library Invalid loaded, but had errors:")
    }

    @Test
    fun withVersion() {
        val versionedIdentifierWithVersion =
            toElmIdentifier("LibraryWithVersion").withVersion("1.0.0")
        val versionedIdentifierNoVersion = toElmIdentifier("LibraryWithVersion")

        val singleLibResult =
            engine.evaluate { library(versionedIdentifierWithVersion) }.onlyResultOrThrow
        assertNotNull(singleLibResult)

        val multiLibResults = engine.evaluate { library(versionedIdentifierWithVersion) }
        assertNotNull(multiLibResults)
        assertNotNull(multiLibResults.getResultFor(versionedIdentifierNoVersion))
    }

    @Test
    fun hedisCompatibility() {
        var libraryResult = engine.evaluate { library("HedisCompatibilityTest") }.onlyResultOrThrow
        var result = libraryResult["QuantityListIncludes"]!!.value
        assertFalse((result as Boolean).value)

        result = libraryResult["ReturnUnspecified"]!!.value
        assertIs<List>(result)
        assertEquals(2, result.count())

        result = libraryResult["ReturnAll"]!!.value
        assertIs<List>(result)
        assertEquals(5, result.count())

        result = libraryResult["ReturnDistinct"]!!.value
        assertIs<List>(result)
        assertEquals(2, result.count())

        result = libraryResult["Test Null Tuple"]!!.value
        assertNull(result)

        engine.state.engineOptions.add(CqlEngine.Options.EnableHedisCompatibilityMode)
        libraryResult = engine.evaluate { library("HedisCompatibilityTest") }.onlyResultOrThrow
        // equivalent semantics for lists
        result = libraryResult["QuantityListIncludes"]!!.value
        assertTrue((result as Boolean).value)

        // no "distinct" behavior for lists
        result = libraryResult["ReturnUnspecified"]!!.value
        assertIs<List>(result)
        assertEquals(5, result.count())

        result = libraryResult["ReturnAll"]!!.value
        assertIs<List>(result)
        assertEquals(5, result.count())

        result = libraryResult["ReturnDistinct"]!!.value
        assertIs<List>(result)
        assertEquals(5, result.count())

        result = libraryResult["Test Null Tuple"]!!.value
        assertNull(result)
    }
}
