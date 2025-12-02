package org.opencds.cqf.cql.engine.execution

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.debug.DebugMap
import org.opencds.cqf.cql.engine.debug.DebugResultEntry
import org.opencds.cqf.cql.engine.exception.CqlException

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

        Assertions.assertNotNull(libraryDebug)

        // Find the debug result for the AnInteger expression
        // It's indexed by location
        val result =
            libraryDebug.keys.stream().filter { e -> e!!.locator == "6:1-6:21" }.findFirst()

        Assertions.assertTrue(result.isPresent)

        val debugResult: MutableList<DebugResultEntry?> = libraryDebug[result.get()]!!
        Assertions.assertEquals(1, debugResult.size)
    }

    @Test
    fun invalidCql() {
        val versionedIdentifier = toElmIdentifier("Invalid")
        val exception =
            Assertions.assertThrows(CqlException::class.java) {
                engine.evaluate { library(versionedIdentifier) }.onlyResultOrThrow
            }
        MatcherAssert.assertThat<String?>(
            exception.message,
            CoreMatchers.containsString("Library Invalid loaded, but had errors:"),
        )
    }

    @Test
    fun withVersion() {
        val versionedIdentifierWithVersion =
            toElmIdentifier("LibraryWithVersion").withVersion("1.0.0")
        val versionedIdentifierNoVersion = toElmIdentifier("LibraryWithVersion")

        val singleLibResult =
            engine.evaluate { library(versionedIdentifierWithVersion) }.onlyResultOrThrow
        Assertions.assertNotNull(singleLibResult)

        val multiLibResults = engine.evaluate { library(versionedIdentifierWithVersion) }
        Assertions.assertNotNull(multiLibResults)
        Assertions.assertNotNull(multiLibResults.getResultFor(versionedIdentifierNoVersion))
    }

    @Test
    fun hedisCompatibility() {
        var libraryResult = engine.evaluate { library("HedisCompatibilityTest") }.onlyResultOrThrow
        var result = libraryResult["QuantityListIncludes"]!!.value
        Assertions.assertFalse((result as Boolean?)!!)

        result = libraryResult["ReturnUnspecified"]!!.value
        Assertions.assertInstanceOf(MutableList::class.java, result)
        Assertions.assertEquals(2, (result as MutableList<*>).size)

        result = libraryResult["ReturnAll"]!!.value
        Assertions.assertInstanceOf(MutableList::class.java, result)
        Assertions.assertEquals(5, (result as MutableList<*>).size)

        result = libraryResult["ReturnDistinct"]!!.value
        Assertions.assertInstanceOf(MutableList::class.java, result)
        Assertions.assertEquals(2, (result as MutableList<*>).size)

        result = libraryResult["Test Null Tuple"]!!.value
        Assertions.assertNull(result)

        engine.state.engineOptions.add(CqlEngine.Options.EnableHedisCompatibilityMode)
        libraryResult = engine.evaluate { library("HedisCompatibilityTest") }.onlyResultOrThrow
        // equivalent semantics for lists
        result = libraryResult["QuantityListIncludes"]!!.value
        Assertions.assertTrue((result as Boolean?)!!)

        // no "distinct" behavior for lists
        result = libraryResult["ReturnUnspecified"]!!.value
        Assertions.assertInstanceOf(MutableList::class.java, result)
        Assertions.assertEquals(5, (result as MutableList<*>).size)

        result = libraryResult["ReturnAll"]!!.value
        Assertions.assertInstanceOf(MutableList::class.java, result)
        Assertions.assertEquals(5, (result as MutableList<*>).size)

        result = libraryResult["ReturnDistinct"]!!.value
        Assertions.assertInstanceOf(MutableList::class.java, result)
        Assertions.assertEquals(5, (result as MutableList<*>).size)

        result = libraryResult["Test Null Tuple"]!!.value
        Assertions.assertNull(result)
    }
}
