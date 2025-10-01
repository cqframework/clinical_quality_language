package org.opencds.cqf.cql.engine.execution

import java.time.ZoneOffset
import java.util.Map
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
        cqlEngineWithNoOptions = CqlEngine(environment, null)
        cqlEngineWithOptions =
            CqlEngine(environment, setOf(CqlEngine.Options.EnableExpressionCaching))
    }

    @Test
    fun nonexistentLibrary() {
        val versionedIdentifier = toElmIdentifier("OtherName")
        val versionedIdentifiers = listOf(versionedIdentifier)
        val newDebugMap = DebugMap()

        val exceptionMessage =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                    cqlEngineWithNoOptions!!.evaluate(
                        versionedIdentifiers,
                        null,
                        null,
                        null,
                        newDebugMap,
                        null,
                    )
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
                    cqlEngineWithOptions!!.evaluate(
                        versionedIdentifiers,
                        null,
                        null,
                        null,
                        newDebugMap,
                        null,
                    )
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
        val evalResultsForMultiLib =
            cqlEngineWithOptions!!.evaluate(
                listOf(libraryIdentifier),
                null,
                null,
                null,
                debugMap,
                null,
            )

        Assertions.assertNotNull(evalResultsForMultiLib)
        val libraryResults = evalResultsForMultiLib.results
        Assertions.assertEquals(1, libraryResults.size)
        Assertions.assertFalse(evalResultsForMultiLib.hasExceptions())

        val evaluationResult = findResultsByLibId(LIBRARY_WITH_VERSION, libraryResults)
        Assertions.assertEquals(5, evaluationResult.expressionResults["Number"]!!.value())
        Assertions.assertEquals(
            _2031_01_01_TO_2032_01_01,
            evaluationResult.expressionResults["Period"]!!.value(),
        )
    }

    // Various bespoke assertions to increase test coverage
    @Test
    fun extraTestCoverage() {
        val versionedIdent =
            VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion(VERSION_1_0_0)
        val versionedIdents = listOf(versionedIdent)

        val evalResultsForMultiLib =
            cqlEngineWithOptions!!.evaluate(versionedIdents, null, null, null, debugMap, null)

        Assertions.assertNotNull(evalResultsForMultiLib)
        Assertions.assertNull(evalResultsForMultiLib.getExceptionFor(versionedIdent))
        Assertions.assertNull(evalResultsForMultiLib.getExceptionFor(versionedIdent))
        Assertions.assertNull(
            evalResultsForMultiLib.getExceptionFor(VersionedIdentifier().withId("fake"))
        )
        Assertions.assertNull(
            evalResultsForMultiLib.getExceptionFor(
                VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion(null)
            )
        )
        Assertions.assertNull(
            evalResultsForMultiLib.getExceptionFor(
                VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion("fake")
            )
        )
    }

    @Test
    fun multipleLibrariesSimple() {
        val evalResultsForMultiLib =
            cqlEngineWithOptions!!.evaluate(
                listOf(MULTI_LIBRARY_1, MULTI_LIBRARY_2, MULTI_LIBRARY_3),
                null,
                null,
                null,
                debugMap,
                null,
            )

        Assertions.assertNotNull(evalResultsForMultiLib)
        val libraryResults = evalResultsForMultiLib.results
        Assertions.assertEquals(3, libraryResults.size)
        Assertions.assertFalse(evalResultsForMultiLib.hasExceptions())

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        Assertions.assertEquals(1, evaluationResult1.expressionResults["Number"]!!.value())
        Assertions.assertEquals(2, evaluationResult2.expressionResults["Number"]!!.value())
        Assertions.assertEquals(3, evaluationResult3.expressionResults["Number"]!!.value())

        Assertions.assertEquals(
            _2021_01_01_TO_2022_01_01,
            evaluationResult1.expressionResults["Period"]!!.value(),
        )
        Assertions.assertEquals(
            _2022_01_01_TO_2023_01_01,
            evaluationResult2.expressionResults["Period"]!!.value(),
        )
        Assertions.assertEquals(
            _2023_01_01_TO_2024_01_01,
            evaluationResult3.expressionResults["Period"]!!.value(),
        )
    }

    @Test
    fun multipleLibrariesMismatchedVersions() {
        val versionedIdentifierBad1 = toElmIdentifier("MultiLibrary1", "bad")
        val versionedIdentifierBad2 = toElmIdentifier("MultiLibrary2", "bad")
        val versionedIdentifierBad3 = toElmIdentifier("MultiLibrary3", "bad")
        val versionedIdentifiers =
            listOf<VersionedIdentifier?>(
                versionedIdentifierBad1,
                versionedIdentifierBad2,
                versionedIdentifierBad3,
            )

        val exception =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                cqlEngineWithOptions!!.evaluate(
                    versionedIdentifiers,
                    null,
                    null,
                    null,
                    debugMap,
                    null,
                )
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
        val evalResultsForMultiLib =
            cqlEngineWithOptions!!.evaluate(
                listOf<VersionedIdentifier?>(MULTI_LIBRARY_1, MULTI_LIBRARY_2, MULTI_LIBRARY_3),
                null,
                null,
                Map.of<String?, Any?>("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null,
            )

        sanityCheckForMultiLib(evalResultsForMultiLib, MULTI_LIBRARY_1)
        sanityCheckForMultiLib(evalResultsForMultiLib, MULTI_LIBRARY_2)
        sanityCheckForMultiLib(evalResultsForMultiLib, MULTI_LIBRARY_3)

        Assertions.assertThrows<IllegalStateException?>(IllegalStateException::class.java) {
            evalResultsForMultiLib.getOnlyResultOrThrow()
        }
        Assertions.assertNotNull(evalResultsForMultiLib)
        val libraryResults = evalResultsForMultiLib.results
        Assertions.assertEquals(3, libraryResults.size)
        Assertions.assertFalse(evalResultsForMultiLib.hasExceptions())

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        Assertions.assertEquals(1, evaluationResult1.expressionResults["Number"]!!.value())
        Assertions.assertEquals(2, evaluationResult2.expressionResults["Number"]!!.value())
        Assertions.assertEquals(3, evaluationResult3.expressionResults["Number"]!!.value())

        Assertions.assertEquals("Uno", evaluationResult1.expressionResults["Name"]!!.value())
        Assertions.assertEquals("Dos", evaluationResult2.expressionResults["Name"]!!.value())
        Assertions.assertEquals("Tres", evaluationResult3.expressionResults["Name"]!!.value())

        Assertions.assertEquals(
            _1900_01_01_TO_1901_01_01,
            evaluationResult1.expressionResults["Period"]!!.value(),
        )
        Assertions.assertEquals(
            _1900_01_01_TO_1901_01_01,
            evaluationResult2.expressionResults["Period"]!!.value(),
        )
        Assertions.assertEquals(
            _1900_01_01_TO_1901_01_01,
            evaluationResult3.expressionResults["Period"]!!.value(),
        )

        // Expressions unique to the libraries in question
        Assertions.assertEquals(
            "MultiLibrary1",
            evaluationResult1.expressionResults["MultiLibraryIdent1"]!!.value(),
        )
        Assertions.assertEquals(
            "One",
            evaluationResult1.expressionResults["MultiLibraryValue1"]!!.value(),
        )

        Assertions.assertEquals(
            "MultiLibrary2",
            evaluationResult2.expressionResults["MultiLibraryIdent2"]!!.value(),
        )
        Assertions.assertEquals(
            "Two",
            evaluationResult2.expressionResults["MultiLibraryValue2"]!!.value(),
        )

        Assertions.assertEquals(
            "MultiLibrary3",
            evaluationResult3.expressionResults["MultiLibraryIdent3"]!!.value(),
        )
        Assertions.assertEquals(
            "Three",
            evaluationResult3.expressionResults["MultiLibraryValue3"]!!.value(),
        )
    }

    @Test
    fun multipleLibrariesWithExpressionUniqueToASingleLib() {
        val evalResultsForMultiLib =
            cqlEngineWithOptions!!.evaluate(
                listOf<VersionedIdentifier?>(
                    MULTI_LIBRARY_1,
                    MULTI_LIBRARY_2,
                    MULTI_LIBRARY_3,
                ), // One expression common to all libraries, one each unique to a different single
                // library
                mutableSetOf<String?>("Number", "MultiLibraryIdent1", "MultiLibraryValue2"),
                null,
                null,
                debugMap,
                null,
            )

        Assertions.assertNotNull(evalResultsForMultiLib)
        val libraryResults = evalResultsForMultiLib.results
        Assertions.assertEquals(0, libraryResults.size)
        Assertions.assertTrue(evalResultsForMultiLib.hasExceptions())
        val exceptions = evalResultsForMultiLib.exceptions
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
        val evalResultsForMultiLib =
            cqlEngineWithOptions!!.evaluate(
                listOf<VersionedIdentifier?>(
                    MULTI_LIBRARY_1,
                    MULTI_LIBRARY_2,
                    MULTI_LIBRARY_3,
                ), // We're leaving out "Name" here
                mutableSetOf<String?>("Number", "Period"),
                null,
                Map.of<String?, Any?>("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null,
            )

        Assertions.assertNotNull(evalResultsForMultiLib)
        val libraryResults = evalResultsForMultiLib.results
        Assertions.assertEquals(3, libraryResults.size)
        Assertions.assertFalse(evalResultsForMultiLib.hasExceptions())
        Assertions.assertTrue(evalResultsForMultiLib.exceptions.isEmpty())
        Assertions.assertFalse(evalResultsForMultiLib.hasWarnings())

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        Assertions.assertEquals(1, evaluationResult1.expressionResults["Number"]!!.value())
        Assertions.assertEquals(2, evaluationResult2.expressionResults["Number"]!!.value())
        Assertions.assertEquals(3, evaluationResult3.expressionResults["Number"]!!.value())

        Assertions.assertEquals(
            _1900_01_01_TO_1901_01_01,
            evaluationResult1.expressionResults["Period"]!!.value(),
        )
        Assertions.assertEquals(
            _1900_01_01_TO_1901_01_01,
            evaluationResult2.expressionResults["Period"]!!.value(),
        )
        Assertions.assertEquals(
            _1900_01_01_TO_1901_01_01,
            evaluationResult3.expressionResults["Period"]!!.value(),
        )

        Assertions.assertNull(evaluationResult1.expressionResults["Name"])
        Assertions.assertNull(evaluationResult2.expressionResults["Name"])
        Assertions.assertNull(evaluationResult3.expressionResults["Name"])
    }

    @Test
    fun singleLibraryInvalid() {
        val evalResultsForMultiLib =
            cqlEngineWithOptions!!.evaluate(
                listOf<VersionedIdentifier?>(toElmIdentifier("MultiLibraryBad", "0.1")),
                null,
                null,
                Map.of<String?, Any?>("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null,
            )

        val exception =
            Assertions.assertThrows(CqlException::class.java) {
                evalResultsForMultiLib.getOnlyResultOrThrow()
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
        val evalResultsForMultiLib =
            cqlEngineWithOptions!!.evaluate(
                listOf<VersionedIdentifier?>(
                    MULTI_LIBRARY_1,
                    MULTI_LIBRARY_2,
                    MULTI_LIBRARY_3,
                    versionedIdentifierBad,
                ),
                null,
                null,
                Map.of<String?, Any?>("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null,
            )

        Assertions.assertNotNull(evalResultsForMultiLib)
        Assertions.assertFalse(evalResultsForMultiLib.exceptions.isEmpty())
        val exceptions = evalResultsForMultiLib.exceptions
        Assertions.assertEquals(1, exceptions.size)
        Assertions.assertTrue(evalResultsForMultiLib.containsExceptionsFor(versionedIdentifierBad))
        // Search for the exception with a versioned identifier:  This should also work
        Assertions.assertTrue(
            evalResultsForMultiLib.containsExceptionsFor(
                VersionedIdentifier().withId("MultiLibraryBad")
            )
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
            exception!!.message,
        )
        Assertions.assertNull(evalResultsForMultiLib.getResultFor(versionedIdentifierBad))

        val libraryResults = evalResultsForMultiLib.results
        Assertions.assertEquals(
            3,
            libraryResults.size,
        ) // there is no eval result for MultiLibraryBad

        val evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults)
        val evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults)
        val evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults)

        Assertions.assertEquals(1, evaluationResult1.expressionResults["Number"]!!.value())
        Assertions.assertEquals(2, evaluationResult2.expressionResults["Number"]!!.value())
        Assertions.assertEquals(3, evaluationResult3.expressionResults["Number"]!!.value())

        Assertions.assertEquals(
            _1900_01_01_TO_1901_01_01,
            evaluationResult1.expressionResults["Period"]!!.value(),
        )
        Assertions.assertEquals(
            _1900_01_01_TO_1901_01_01,
            evaluationResult2.expressionResults["Period"]!!.value(),
        )
        Assertions.assertEquals(
            _1900_01_01_TO_1901_01_01,
            evaluationResult3.expressionResults["Period"]!!.value(),
        )
    }

    private fun findResultsByLibId(
        libId: String?,
        results: MutableMap<VersionedIdentifier?, EvaluationResult?>,
    ): EvaluationResult {
        return results.entries
            .stream()
            .filter { x: MutableMap.MutableEntry<VersionedIdentifier?, EvaluationResult?>? ->
                x!!.key!!.id.equals(libId)
            }
            .map<EvaluationResult> { it.value }
            .findFirst()
            .orElseThrow()
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
            evalResultsForMultiLib: EvaluationResultsForMultiLib,
            libraryIdentifier: VersionedIdentifier,
        ) {
            Assertions.assertTrue(evalResultsForMultiLib.containsResultsFor(libraryIdentifier))

            val bogusId = VersionedIdentifier().withId("bogus")
            Assertions.assertFalse(evalResultsForMultiLib.containsResultsFor(bogusId))
            Assertions.assertFalse(
                evalResultsForMultiLib.containsWarningsFor(VersionedIdentifier().withId("bogus"))
            )
            Assertions.assertFalse(
                evalResultsForMultiLib.containsExceptionsFor(VersionedIdentifier().withId("bogus"))
            )

            val bogusVersion = libraryIdentifier.withVersion("bogus")
            Assertions.assertFalse(evalResultsForMultiLib.containsResultsFor(bogusVersion))
            Assertions.assertFalse(evalResultsForMultiLib.containsWarningsFor(bogusVersion))
            Assertions.assertFalse(evalResultsForMultiLib.containsExceptionsFor(bogusVersion))

            // versionless identifier searches should work as well
            Assertions.assertTrue(
                evalResultsForMultiLib.containsResultsFor(libraryIdentifier.withVersion(null))
            )
            Assertions.assertThrows(IllegalStateException::class.java) {
                evalResultsForMultiLib.getOnlyResultOrThrow()
            }
            Assertions.assertFalse(evalResultsForMultiLib.containsExceptionsFor(libraryIdentifier))
            Assertions.assertFalse(evalResultsForMultiLib.containsWarningsFor(libraryIdentifier))
            Assertions.assertTrue(evalResultsForMultiLib.exceptions.isEmpty())
            Assertions.assertTrue(evalResultsForMultiLib.warnings.isEmpty())
            Assertions.assertNotNull(evalResultsForMultiLib.getResultFor(libraryIdentifier))
            Assertions.assertNull(evalResultsForMultiLib.getExceptionFor(libraryIdentifier))
            Assertions.assertNull(evalResultsForMultiLib.getWarningFor(libraryIdentifier))
        }
    }
}
