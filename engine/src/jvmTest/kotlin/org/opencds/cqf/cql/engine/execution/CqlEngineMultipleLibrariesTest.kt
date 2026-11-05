package org.opencds.cqf.cql.engine.execution

import java.time.ZoneOffset
import java.util.stream.Stream
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.cqframework.cql.cql2elm.CqlIncludeException
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.debug.DebugMap
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlEngineMultipleLibrariesTest : CqlTestBase() {
    private var debugMap: DebugMap? = null
    private var cqlEngineWithNoOptions: CqlEngine? = null
    private var cqlEngineWithOptions: CqlEngine? = null

    override val cqlSubdirectory: String
        get() = "multilib"

    @BeforeEach
    fun setup() {
        debugMap = DebugMap()
        cqlEngineWithNoOptions = CqlEngine(environment!!, null)
        cqlEngineWithOptions =
            CqlEngine(environment!!, mutableSetOf(CqlEngine.Options.EnableExpressionCaching))
    }

    @Test
    fun nonexistentLibrary() {
        val versionedIdentifier = toElmIdentifier("OtherName")
        val versionedIdentifiers = listOf(versionedIdentifier)
        val newDebugMap = DebugMap()

        val exceptionMessage =
            assertFailsWith<CqlIncludeException> {
                    cqlEngineWithNoOptions!!.evaluate {
                        for (id in versionedIdentifiers) {
                            library(id)
                        }
                        debugMap = newDebugMap
                    }
                }
                .message

        assertEquals(
            "Could not load source for library OtherName, version null, namespace uri null.",
            exceptionMessage,
        )
    }

    @Test
    fun libraryAndCqlMismatchedNames() {
        val versionedIdentifier = toElmIdentifier("NameMismatch")
        val versionedIdentifiers = listOf(versionedIdentifier)
        val newDebugMap = DebugMap()

        // The existing single library evaluation will tolerate name mismatches, however, this
        // violates the CQL standard
        // Fixing that bug is out of scope for this work, but we definitely want to do the right
        // thing going forward
        // with the new multiple library evaluation.
        val exceptionMessage =
            assertFailsWith<CqlIncludeException> {
                    cqlEngineWithOptions!!.evaluate {
                        for (id in versionedIdentifiers) {
                            library(id)
                        }
                        debugMap = newDebugMap
                    }
                }
                .message

        assertEquals(
            "Library NameMismatch was included with version null, but id: MismatchName and version null of the library was found.",
            exceptionMessage,
        )
    }

    @ParameterizedTest
    @MethodSource("libraryWithVersionQueriesParams")
    fun libraryWithVersionQueries(libraryIdentifier: VersionedIdentifier) {
        val evaluationResults =
            cqlEngineWithOptions!!.evaluate {
                library(libraryIdentifier)
                debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
            }

        assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        assertEquals(1, libraryResults.size)
        assertFalse(evaluationResults.hasExceptions())

        val evaluationResult = findResultsByLibId(LIBRARY_WITH_VERSION, libraryResults)
        assertEquals(5.toCqlInteger(), evaluationResult["Number"]!!.value)
        assertEquals(
            true,
            equivalent(_2031_01_01_TO_2032_01_01, evaluationResult["Period"]!!.value).value,
        )
    }

    // Various bespoke assertions to increase test coverage
    @Test
    fun extraTestCoverage() {
        val versionedIdent =
            VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion(VERSION_1_0_0)
        val versionedIdents = listOf(versionedIdent)

        val evaluationResults =
            cqlEngineWithOptions!!.evaluate {
                for (id in versionedIdents) {
                    library(id)
                }
                debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
            }

        assertNotNull(evaluationResults)
        assertNull(evaluationResults.getExceptionFor(versionedIdent))
        assertNull(evaluationResults.getExceptionFor(versionedIdent))
        assertNull(evaluationResults.getExceptionFor(VersionedIdentifier().withId("fake")))
        assertNull(
            evaluationResults.getExceptionFor(
                VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion(null)
            )
        )
        assertNull(
            evaluationResults.getExceptionFor(
                VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion("fake")
            )
        )
    }

    @Test
    fun multipleLibrariesSimple() {
        val evaluationResults =
            cqlEngineWithOptions!!.evaluate {
                library(MULTI_LIBRARY_1)
                library(MULTI_LIBRARY_2)
                library(MULTI_LIBRARY_3)
                debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
            }

        assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        assertEquals(3, libraryResults.size)
        assertFalse(evaluationResults.hasExceptions())

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        assertEquals(Integer.ONE, evaluationResult1["Number"]!!.value)
        assertEquals(2.toCqlInteger(), evaluationResult2["Number"]!!.value)
        assertEquals(3.toCqlInteger(), evaluationResult3["Number"]!!.value)

        assertEquals(
            true,
            equivalent(_2021_01_01_TO_2022_01_01, evaluationResult1["Period"]!!.value).value,
        )
        assertEquals(
            true,
            equivalent(_2022_01_01_TO_2023_01_01, evaluationResult2["Period"]!!.value).value,
        )
        assertEquals(
            true,
            equivalent(_2023_01_01_TO_2024_01_01, evaluationResult3["Period"]!!.value).value,
        )
    }

    @Test
    fun multipleLibrariesMismatchedVersions() {
        val versionedIdentifierBad1 = toElmIdentifier("MultiLibrary1", "bad")
        val versionedIdentifierBad2 = toElmIdentifier("MultiLibrary2", "bad")
        val versionedIdentifierBad3 = toElmIdentifier("MultiLibrary3", "bad")
        val versionedIdentifiers =
            listOf(versionedIdentifierBad1, versionedIdentifierBad2, versionedIdentifierBad3)

        val exception =
            assertFailsWith<CqlIncludeException> {
                cqlEngineWithOptions!!.evaluate {
                    for (id in versionedIdentifiers) {
                        library(id)
                    }
                    debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
                }
            }

        assertContains(
            exception.message!!,
            "Library MultiLibrary1 was included with version bad, but id: MultiLibrary1 and version 1.0.0 of the library was found.",
        )
    }

    @Test
    fun multipleLibrariesWithParameters() {
        val evaluationResults =
            cqlEngineWithOptions!!.evaluate {
                library(MULTI_LIBRARY_1)
                library(MULTI_LIBRARY_2)
                library(MULTI_LIBRARY_3)
                parameters = mapOf("Measurement Period" to _1900_01_01_TO_1901_01_01)
                debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
            }

        sanityCheckForMultiLib(evaluationResults, MULTI_LIBRARY_1)
        sanityCheckForMultiLib(evaluationResults, MULTI_LIBRARY_2)
        sanityCheckForMultiLib(evaluationResults, MULTI_LIBRARY_3)

        assertFailsWith<IllegalStateException> { evaluationResults.onlyResultOrThrow }
        assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        assertEquals(3, libraryResults.size)
        assertFalse(evaluationResults.hasExceptions())

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        assertEquals(Integer.ONE, evaluationResult1["Number"]!!.value)
        assertEquals(2.toCqlInteger(), evaluationResult2["Number"]!!.value)
        assertEquals(3.toCqlInteger(), evaluationResult3["Number"]!!.value)

        assertEquals("Uno".toCqlString(), evaluationResult1["Name"]!!.value)
        assertEquals("Dos".toCqlString(), evaluationResult2["Name"]!!.value)
        assertEquals("Tres".toCqlString(), evaluationResult3["Name"]!!.value)

        assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult1["Period"]!!.value)
        assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult2["Period"]!!.value)
        assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult3["Period"]!!.value)

        // Expressions unique to the libraries in question
        assertEquals("MultiLibrary1".toCqlString(), evaluationResult1["MultiLibraryIdent1"]!!.value)
        assertEquals("One".toCqlString(), evaluationResult1["MultiLibraryValue1"]!!.value)

        assertEquals("MultiLibrary2".toCqlString(), evaluationResult2["MultiLibraryIdent2"]!!.value)
        assertEquals("Two".toCqlString(), evaluationResult2["MultiLibraryValue2"]!!.value)

        assertEquals("MultiLibrary3".toCqlString(), evaluationResult3["MultiLibraryIdent3"]!!.value)
        assertEquals("Three".toCqlString(), evaluationResult3["MultiLibraryValue3"]!!.value)
    }

    @Test
    fun multipleLibrariesWithExpressionUniqueToASingleLib() {
        val evaluationResults =
            cqlEngineWithOptions!!.evaluate {
                // One expression common to all libraries, one each unique to a different single
                // library
                val expressions = listOf("Number", "MultiLibraryIdent1", "MultiLibraryValue2")
                library(MULTI_LIBRARY_1) { expressions(expressions) }
                library(MULTI_LIBRARY_2) { expressions(expressions) }
                library(MULTI_LIBRARY_3) { expressions(expressions) }
                debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
            }

        assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        assertEquals(0, libraryResults.size)
        assertTrue(evaluationResults.hasExceptions())
        val exceptions = evaluationResults.exceptions
        assertEquals(3, exceptions.size)

        assertContains(
            exceptions[MULTI_LIBRARY_1]!!.message!!,
            "Could not resolve expression reference 'MultiLibraryValue2' in library 'MultiLibrary1'.",
        )
        assertContains(
            exceptions[MULTI_LIBRARY_2]!!.message!!,
            "Could not resolve expression reference 'MultiLibraryIdent1' in library 'MultiLibrary2'.",
        )
        // We may get either one of two exceptions here, depending on the order of evaluation
        assertContains(
            exceptions[MULTI_LIBRARY_3]!!.message!!,
            "Could not resolve expression reference",
        )
    }

    @Test
    fun multipleLibrariesWithSubsetOfAllCommonExpressions() {
        val evaluationResults =
            cqlEngineWithOptions!!.evaluate {
                // We're leaving out "Name" here
                val expressions = listOf("Number", "Period")
                library(MULTI_LIBRARY_1) { expressions(expressions) }
                library(MULTI_LIBRARY_2) { expressions(expressions) }
                library(MULTI_LIBRARY_3) { expressions(expressions) }
                parameters = mapOf("Measurement Period" to _1900_01_01_TO_1901_01_01)
                debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
            }

        assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        assertEquals(3, libraryResults.size)
        assertFalse(evaluationResults.hasExceptions())
        assertTrue(evaluationResults.exceptions.isEmpty())
        assertFalse(evaluationResults.hasWarnings())

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        assertEquals(Integer.ONE, evaluationResult1["Number"]!!.value)
        assertEquals(2.toCqlInteger(), evaluationResult2["Number"]!!.value)
        assertEquals(3.toCqlInteger(), evaluationResult3["Number"]!!.value)

        assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult1["Period"]!!.value)
        assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult2["Period"]!!.value)
        assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult3["Period"]!!.value)

        assertNull(evaluationResult1["Name"])
        assertNull(evaluationResult2["Name"])
        assertNull(evaluationResult3["Name"])
    }

    @Test
    fun singleLibraryInvalid() {
        val evaluationResults =
            cqlEngineWithOptions!!.evaluate {
                library(toElmIdentifier("MultiLibraryBad", "0.1"))
                parameters = mapOf("Measurement Period" to _1900_01_01_TO_1901_01_01)
                debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
            }

        val exception = assertFailsWith<CqlException> { evaluationResults.onlyResultOrThrow }
        assertContains(
            exception.message!!,
            "Library MultiLibraryBad-0.1 loaded, but had errors: Syntax error at define",
        )
    }

    @Test
    fun multipleLibrariesOneInvalid() {
        val versionedIdentifierBad = toElmIdentifier("MultiLibraryBad", "0.1")
        val evaluationResults =
            cqlEngineWithOptions!!.evaluate {
                library(MULTI_LIBRARY_1)
                library(MULTI_LIBRARY_2)
                library(MULTI_LIBRARY_3)
                library(versionedIdentifierBad)
                parameters = mapOf("Measurement Period" to _1900_01_01_TO_1901_01_01)
                debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
            }

        assertNotNull(evaluationResults)
        assertFalse(evaluationResults.exceptions.isEmpty())
        val exceptions = evaluationResults.exceptions
        assertEquals(1, exceptions.size)
        assertTrue(evaluationResults.containsExceptionsFor(versionedIdentifierBad))
        // Search for the exception with a versioned identifier:  This should also work
        assertTrue(
            evaluationResults.containsExceptionsFor(VersionedIdentifier().withId("MultiLibraryBad"))
        )
        val exceptionEntry = exceptions.entries.iterator().next()
        assertEquals(
            VersionedIdentifier().withId("MultiLibraryBad").withVersion("0.1"),
            exceptionEntry.key,
        )
        val exception = exceptionEntry.value
        assertNotNull(exception)
        assertEquals(
            "Library MultiLibraryBad-0.1 loaded, but had errors: Syntax error at define",
            exception.message,
        )
        assertNull(evaluationResults.getResultFor(versionedIdentifierBad))

        val libraryResults = evaluationResults.results
        assertEquals(3, libraryResults.size) // there is no eval result for MultiLibraryBad

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        assertEquals(Integer.ONE, evaluationResult1["Number"]!!.value)
        assertEquals(2.toCqlInteger(), evaluationResult2["Number"]!!.value)
        assertEquals(3.toCqlInteger(), evaluationResult3["Number"]!!.value)

        assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult1["Period"]!!.value)
        assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult2["Period"]!!.value)
        assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult3["Period"]!!.value)
    }

    private fun findResultsByLibId(
        libId: String,
        results: Map<VersionedIdentifier, EvaluationResult>,
    ): EvaluationResult {
        return results.filter { x -> x.key.id.equals(libId) }.map { it.value }.first()
    }

    companion object {
        private val _1900_01_01 = DateTime("1900-01-01", ZoneOffset.UTC)
        private val _1901_01_01 = DateTime("1901-01-01", ZoneOffset.UTC)
        private val _2021_01_01 = DateTime("2021-01-01", ZoneOffset.UTC)
        private val _2022_01_01 = DateTime("2022-01-01", ZoneOffset.UTC)
        private val _2023_01_01 = DateTime("2023-01-01", ZoneOffset.UTC)
        private val _2024_01_01 = DateTime("2024-01-01", ZoneOffset.UTC)
        private val _2031_01_01 = DateTime("2031-01-01", ZoneOffset.UTC)
        private val _2032_01_01 = DateTime("2032-01-01", ZoneOffset.UTC)

        private val _1900_01_01_TO_1901_01_01 = Interval(_1900_01_01, true, _1901_01_01, false)
        private val _2021_01_01_TO_2022_01_01 = Interval(_2021_01_01, true, _2022_01_01, false)
        private val _2022_01_01_TO_2023_01_01 = Interval(_2022_01_01, true, _2023_01_01, false)
        private val _2023_01_01_TO_2024_01_01 = Interval(_2023_01_01, true, _2024_01_01, false)
        private val _2031_01_01_TO_2032_01_01 = Interval(_2031_01_01, true, _2032_01_01, false)
        private val MULTI_LIBRARY_1: VersionedIdentifier = toElmIdentifier("MultiLibrary1")
        private val MULTI_LIBRARY_2: VersionedIdentifier = toElmIdentifier("MultiLibrary2")
        private val MULTI_LIBRARY_3: VersionedIdentifier = toElmIdentifier("MultiLibrary3")
        private const val LIBRARY_WITH_VERSION = "LibraryWithVersion"
        private const val VERSION_1_0_0 = "1.0.0"

        @JvmStatic
        private fun libraryWithVersionQueriesParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(toElmIdentifier(LIBRARY_WITH_VERSION)),
                Arguments.of(toElmIdentifier(LIBRARY_WITH_VERSION, VERSION_1_0_0)),
            )
        }

        private fun sanityCheckForMultiLib(
            evaluationResults: EvaluationResults,
            libraryIdentifier: VersionedIdentifier,
        ) {
            assertTrue(evaluationResults.containsResultsFor(libraryIdentifier))

            val bogusId = VersionedIdentifier().withId("bogus")
            assertFalse(evaluationResults.containsResultsFor(bogusId))
            assertFalse(
                evaluationResults.containsWarningsFor(VersionedIdentifier().withId("bogus"))
            )
            assertFalse(
                evaluationResults.containsExceptionsFor(VersionedIdentifier().withId("bogus"))
            )

            val bogusVersion = libraryIdentifier.withVersion("bogus")
            assertFalse(evaluationResults.containsResultsFor(bogusVersion))
            assertFalse(evaluationResults.containsWarningsFor(bogusVersion))
            assertFalse(evaluationResults.containsExceptionsFor(bogusVersion))

            // versionless identifier searches should work as well
            assertTrue(evaluationResults.containsResultsFor(libraryIdentifier.withVersion(null)))
            assertFailsWith<IllegalStateException> { evaluationResults.onlyResultOrThrow }
            assertFalse(evaluationResults.containsExceptionsFor(libraryIdentifier))
            assertFalse(evaluationResults.containsWarningsFor(libraryIdentifier))
            assertTrue(evaluationResults.exceptions.isEmpty())
            assertTrue(evaluationResults.warnings.isEmpty())
            assertNotNull(evaluationResults.getResultFor(libraryIdentifier))
            assertNull(evaluationResults.getExceptionFor(libraryIdentifier))
            assertNull(evaluationResults.getWarningFor(libraryIdentifier))
        }
    }
}
