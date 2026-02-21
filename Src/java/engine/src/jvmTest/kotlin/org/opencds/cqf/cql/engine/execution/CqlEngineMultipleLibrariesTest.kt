package org.opencds.cqf.cql.engine.execution

import java.time.ZoneOffset
import java.util.stream.Stream
import org.cqframework.cql.cql2elm.CqlIncludeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.debug.DebugMap
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval

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
            Assertions.assertThrows(CqlIncludeException::class.java) {
                    cqlEngineWithNoOptions!!.evaluate {
                        for (id in versionedIdentifiers) {
                            library(id)
                        }
                        debugMap = newDebugMap
                    }
                }
                .message

        Assertions.assertEquals(
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
            Assertions.assertThrows(CqlIncludeException::class.java) {
                    cqlEngineWithOptions!!.evaluate {
                        for (id in versionedIdentifiers) {
                            library(id)
                        }
                        debugMap = newDebugMap
                    }
                }
                .message

        Assertions.assertEquals(
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

        Assertions.assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        Assertions.assertEquals(1, libraryResults.size)
        Assertions.assertFalse(evaluationResults.hasExceptions())

        val evaluationResult = findResultsByLibId(LIBRARY_WITH_VERSION, libraryResults)
        Assertions.assertEquals(5, evaluationResult["Number"]!!.value)
        Assertions.assertEquals(
            true,
            equivalent(_2031_01_01_TO_2032_01_01, evaluationResult["Period"]!!.value),
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

        Assertions.assertNotNull(evaluationResults)
        Assertions.assertNull(evaluationResults.getExceptionFor(versionedIdent))
        Assertions.assertNull(evaluationResults.getExceptionFor(versionedIdent))
        Assertions.assertNull(
            evaluationResults.getExceptionFor(VersionedIdentifier().withId("fake"))
        )
        Assertions.assertNull(
            evaluationResults.getExceptionFor(
                VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion(null)
            )
        )
        Assertions.assertNull(
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

        Assertions.assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        Assertions.assertEquals(3, libraryResults.size)
        Assertions.assertFalse(evaluationResults.hasExceptions())

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        Assertions.assertEquals(1, evaluationResult1["Number"]!!.value)
        Assertions.assertEquals(2, evaluationResult2["Number"]!!.value)
        Assertions.assertEquals(3, evaluationResult3["Number"]!!.value)

        Assertions.assertEquals(
            true,
            equivalent(_2021_01_01_TO_2022_01_01, evaluationResult1["Period"]!!.value),
        )
        Assertions.assertEquals(
            true,
            equivalent(_2022_01_01_TO_2023_01_01, evaluationResult2["Period"]!!.value),
        )
        Assertions.assertEquals(
            true,
            equivalent(_2023_01_01_TO_2024_01_01, evaluationResult3["Period"]!!.value),
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
            Assertions.assertThrows(CqlIncludeException::class.java) {
                cqlEngineWithOptions!!.evaluate {
                    for (id in versionedIdentifiers) {
                        library(id)
                    }
                    debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
                }
            }

        assertThat(
            exception.message,
            Matchers.containsString(
                "Library MultiLibrary1 was included with version bad, but id: MultiLibrary1 and version 1.0.0 of the library was found."
            ),
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

        Assertions.assertThrows(IllegalStateException::class.java) {
            evaluationResults.onlyResultOrThrow
        }
        Assertions.assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        Assertions.assertEquals(3, libraryResults.size)
        Assertions.assertFalse(evaluationResults.hasExceptions())

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        Assertions.assertEquals(1, evaluationResult1["Number"]!!.value)
        Assertions.assertEquals(2, evaluationResult2["Number"]!!.value)
        Assertions.assertEquals(3, evaluationResult3["Number"]!!.value)

        Assertions.assertEquals("Uno", evaluationResult1["Name"]!!.value)
        Assertions.assertEquals("Dos", evaluationResult2["Name"]!!.value)
        Assertions.assertEquals("Tres", evaluationResult3["Name"]!!.value)

        Assertions.assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult1["Period"]!!.value)
        Assertions.assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult2["Period"]!!.value)
        Assertions.assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult3["Period"]!!.value)

        // Expressions unique to the libraries in question
        Assertions.assertEquals("MultiLibrary1", evaluationResult1["MultiLibraryIdent1"]!!.value)
        Assertions.assertEquals("One", evaluationResult1["MultiLibraryValue1"]!!.value)

        Assertions.assertEquals("MultiLibrary2", evaluationResult2["MultiLibraryIdent2"]!!.value)
        Assertions.assertEquals("Two", evaluationResult2["MultiLibraryValue2"]!!.value)

        Assertions.assertEquals("MultiLibrary3", evaluationResult3["MultiLibraryIdent3"]!!.value)
        Assertions.assertEquals("Three", evaluationResult3["MultiLibraryValue3"]!!.value)
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

        Assertions.assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        Assertions.assertEquals(0, libraryResults.size)
        Assertions.assertTrue(evaluationResults.hasExceptions())
        val exceptions = evaluationResults.exceptions
        Assertions.assertEquals(3, exceptions.size)

        assertThat<String?>(
            exceptions[MULTI_LIBRARY_1]!!.message,
            Matchers.containsString(
                "Could not resolve expression reference 'MultiLibraryValue2' in library 'MultiLibrary1'."
            ),
        )
        assertThat<String?>(
            exceptions[MULTI_LIBRARY_2]!!.message,
            Matchers.containsString(
                "Could not resolve expression reference 'MultiLibraryIdent1' in library 'MultiLibrary2'."
            ),
        )
        // We may get either one of two exceptions here, depending on the order of evaluation
        assertThat<String?>(
            exceptions[MULTI_LIBRARY_3]!!.message,
            Matchers.containsString("Could not resolve expression reference"),
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

        Assertions.assertNotNull(evaluationResults)
        val libraryResults = evaluationResults.results
        Assertions.assertEquals(3, libraryResults.size)
        Assertions.assertFalse(evaluationResults.hasExceptions())
        Assertions.assertTrue(evaluationResults.exceptions.isEmpty())
        Assertions.assertFalse(evaluationResults.hasWarnings())

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        Assertions.assertEquals(1, evaluationResult1["Number"]!!.value)
        Assertions.assertEquals(2, evaluationResult2["Number"]!!.value)
        Assertions.assertEquals(3, evaluationResult3["Number"]!!.value)

        Assertions.assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult1["Period"]!!.value)
        Assertions.assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult2["Period"]!!.value)
        Assertions.assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult3["Period"]!!.value)

        Assertions.assertNull(evaluationResult1["Name"])
        Assertions.assertNull(evaluationResult2["Name"])
        Assertions.assertNull(evaluationResult3["Name"])
    }

    @Test
    fun singleLibraryInvalid() {
        val evaluationResults =
            cqlEngineWithOptions!!.evaluate {
                library(toElmIdentifier("MultiLibraryBad", "0.1"))
                parameters = mapOf("Measurement Period" to _1900_01_01_TO_1901_01_01)
                debugMap = this@CqlEngineMultipleLibrariesTest.debugMap
            }

        val exception =
            Assertions.assertThrows(CqlException::class.java) {
                evaluationResults.onlyResultOrThrow
            }
        assertThat<String?>(
            exception.message,
            Matchers.containsString(
                "Library MultiLibraryBad-0.1 loaded, but had errors: Syntax error at define"
            ),
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

        Assertions.assertNotNull(evaluationResults)
        Assertions.assertFalse(evaluationResults.exceptions.isEmpty())
        val exceptions = evaluationResults.exceptions
        Assertions.assertEquals(1, exceptions.size)
        Assertions.assertTrue(evaluationResults.containsExceptionsFor(versionedIdentifierBad))
        // Search for the exception with a versioned identifier:  This should also work
        Assertions.assertTrue(
            evaluationResults.containsExceptionsFor(VersionedIdentifier().withId("MultiLibraryBad"))
        )
        val exceptionEntry = exceptions.entries.iterator().next()
        Assertions.assertEquals(
            VersionedIdentifier().withId("MultiLibraryBad").withVersion("0.1"),
            exceptionEntry.key,
        )
        val exception = exceptionEntry.value
        Assertions.assertNotNull(exception)
        Assertions.assertEquals(
            "Library MultiLibraryBad-0.1 loaded, but had errors: Syntax error at define",
            exception.message,
        )
        Assertions.assertNull(evaluationResults.getResultFor(versionedIdentifierBad))

        val libraryResults = evaluationResults.results
        Assertions.assertEquals(
            3,
            libraryResults.size,
        ) // there is no eval result for MultiLibraryBad

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        Assertions.assertEquals(1, evaluationResult1["Number"]!!.value)
        Assertions.assertEquals(2, evaluationResult2["Number"]!!.value)
        Assertions.assertEquals(3, evaluationResult3["Number"]!!.value)

        Assertions.assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult1["Period"]!!.value)
        Assertions.assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult2["Period"]!!.value)
        Assertions.assertEquals(_1900_01_01_TO_1901_01_01, evaluationResult3["Period"]!!.value)
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
            Assertions.assertTrue(evaluationResults.containsResultsFor(libraryIdentifier))

            val bogusId = VersionedIdentifier().withId("bogus")
            Assertions.assertFalse(evaluationResults.containsResultsFor(bogusId))
            Assertions.assertFalse(
                evaluationResults.containsWarningsFor(VersionedIdentifier().withId("bogus"))
            )
            Assertions.assertFalse(
                evaluationResults.containsExceptionsFor(VersionedIdentifier().withId("bogus"))
            )

            val bogusVersion = libraryIdentifier.withVersion("bogus")
            Assertions.assertFalse(evaluationResults.containsResultsFor(bogusVersion))
            Assertions.assertFalse(evaluationResults.containsWarningsFor(bogusVersion))
            Assertions.assertFalse(evaluationResults.containsExceptionsFor(bogusVersion))

            // versionless identifier searches should work as well
            Assertions.assertTrue(
                evaluationResults.containsResultsFor(libraryIdentifier.withVersion(null))
            )
            Assertions.assertThrows(IllegalStateException::class.java) {
                evaluationResults.onlyResultOrThrow
            }
            Assertions.assertFalse(evaluationResults.containsExceptionsFor(libraryIdentifier))
            Assertions.assertFalse(evaluationResults.containsWarningsFor(libraryIdentifier))
            Assertions.assertTrue(evaluationResults.exceptions.isEmpty())
            Assertions.assertTrue(evaluationResults.warnings.isEmpty())
            Assertions.assertNotNull(evaluationResults.getResultFor(libraryIdentifier))
            Assertions.assertNull(evaluationResults.getExceptionFor(libraryIdentifier))
            Assertions.assertNull(evaluationResults.getWarningFor(libraryIdentifier))
        }
    }
}
