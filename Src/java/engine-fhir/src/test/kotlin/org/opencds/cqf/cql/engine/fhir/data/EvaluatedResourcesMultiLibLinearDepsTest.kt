package org.opencds.cqf.cql.engine.fhir.data

import org.cqframework.cql.cql2elm.CqlIncludeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.EvaluationResultsForMultiLib

internal class EvaluatedResourcesMultiLibLinearDepsTest : FhirExecutionMultiLibTestBase() {
    @ParameterizedTest
    @MethodSource("singleLibParams")
    fun singleLib(
        libId: VersionedIdentifier,
        expressionName: String,
        expectedResources: List<IBaseResource>,
        expectedValues: List<IBaseResource>,
        expressionCaching: Boolean,
    ) {
        val engine = getCqlEngineForFhirNewLibMgr(expressionCaching)

        // Old single-lib API
        val singleResult = engine.evaluate(libId, setOf<String?>(expressionName))

        EvaluatedResourceTestUtils.assertEvaluationResult(
            singleResult,
            expressionName,
            expectedResources,
            expectedValues,
        )

        // Old multi-lib API passing a single lib
        val multiResults =
            engine.evaluate(listOf<VersionedIdentifier?>(libId), setOf<String?>(expressionName))
        Assertions.assertTrue(multiResults.containsResultsFor(libId))
        Assertions.assertFalse(multiResults.containsExceptionsFor(libId))
        val multiResultFor = multiResults.getResultFor(libId)
        val multiResult = multiResults.getOnlyResultOrThrow()
        Assertions.assertNull(multiResults.getExceptionFor(libId))

        // Sanity check:  the single result and the multi-lib result should be the same
        Assertions.assertEquals(
            multiResultFor.expressionResults.size,
            multiResult.expressionResults.size,
        )

        EvaluatedResourceTestUtils.assertEvaluationResult(
            multiResult,
            expressionName,
            expectedResources,
            expectedValues,
        )
    }

    @Test
    fun singleLibNonexistentLib() {
        val engine = getCqlEngineForFhirNewLibMgr(false)
        val versionedIdentifier = VersionedIdentifier().withId("bad")

        // Old single-lib API
        val singleLibException =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                engine.evaluate(versionedIdentifier)
            }

        assertThat(
            singleLibException.message,
            Matchers.startsWith("Could not load source for library bad"),
        )

        val versionedIdentifiers = listOf<VersionedIdentifier?>(versionedIdentifier)
        // Old multi-lib API passing a single lib
        val multiLibException =
            Assertions.assertThrows(CqlIncludeException::class.java) {
                engine.evaluate(versionedIdentifiers, null)
            }

        assertThat(
            multiLibException.message,
            Matchers.startsWith("Could not load source for library bad"),
        )
    }

    @Test
    fun ensureWarningsAreSeparateFromErrors() {
        val engine = getCqlEngineForFhirNewLibMgr(true)

        val multiLibResults =
            engine.evaluate(
                listOf<VersionedIdentifier?>(
                    LIB_1,
                    LIB_WARNING_HIDING,
                    LIB_ERROR_INVALID_CAST_EXPRESSION,
                ),
                null,
            )

        Assertions.assertTrue(multiLibResults.hasExceptions())
        Assertions.assertTrue(multiLibResults.hasWarnings())
        Assertions.assertFalse(multiLibResults.warnings.isEmpty())
        Assertions.assertFalse(multiLibResults.exceptions.isEmpty())

        Assertions.assertTrue(multiLibResults.containsResultsFor(LIB_1))
        Assertions.assertFalse(multiLibResults.containsWarningsFor(LIB_1))
        Assertions.assertFalse(multiLibResults.containsExceptionsFor(LIB_1))

        Assertions.assertTrue(multiLibResults.containsResultsFor(LIB_WARNING_HIDING))
        Assertions.assertTrue(multiLibResults.containsWarningsFor(LIB_WARNING_HIDING))
        Assertions.assertFalse(multiLibResults.containsExceptionsFor(LIB_WARNING_HIDING))

        val warning = multiLibResults.getWarningFor(LIB_WARNING_HIDING)
        Assertions.assertInstanceOf(CqlException::class.java, warning)
        Assertions.assertTrue(
            warning!!
                .message!!
                .contains(
                    "An alias identifier Definition is hiding another identifier of the same name."
                )
        )

        Assertions.assertFalse(
            multiLibResults.containsResultsFor(LIB_ERROR_INVALID_CAST_EXPRESSION)
        )
        Assertions.assertFalse(
            multiLibResults.containsWarningsFor(LIB_ERROR_INVALID_CAST_EXPRESSION)
        )
        Assertions.assertTrue(
            multiLibResults.containsExceptionsFor(LIB_ERROR_INVALID_CAST_EXPRESSION)
        )

        val error = multiLibResults.getExceptionFor(LIB_ERROR_INVALID_CAST_EXPRESSION)
        Assertions.assertInstanceOf(CqlException::class.java, error)
        Assertions.assertTrue(
            error!!
                .message!!
                .contains(
                    "Expression of type 'List of System.Integer' cannot be cast as a value of type 'System.Integer'."
                )
        )
    }

    // Note that there's no obvious way to set up a scenario in which a multilib evaluation will
    // result in different
    // evaluated resources among the libraries in the same evaluation, but this test can and will
    // assert different
    // values
    // for each library.
    @ParameterizedTest
    @MethodSource("multiLibParams")
    fun multiLib(
        libId: VersionedIdentifier?,
        expressionName: String,
        expectedResources: List<IBaseResource>,
        expectedValues: List<IBaseResource>,
        expressionCaching: Boolean,
    ) {
        val engine = getCqlEngineForFhirNewLibMgr(expressionCaching)

        val results: EvaluationResultsForMultiLib =
            engine.evaluate(ALL_LIB_IDS, setOf<String?>(expressionName))

        val evaluationResultForIdentifier = results.getResultFor(libId)

        EvaluatedResourceTestUtils.assertEvaluationResult(
            evaluationResultForIdentifier,
            expressionName,
            expectedResources,
            expectedValues,
        )
    }

    @ParameterizedTest
    @MethodSource("multiLibEnsurePartialCacheAllowsUncachedLibsToBeCompiledParams")
    fun multiLibEnsurePartialCacheAllowsUncachedLibsToBeCompiled(expressionCaching: Boolean) {
        val engine = getCqlEngineForFhirNewLibMgr(expressionCaching)

        // Compile only one library:  it will be cached
        val resultsSingleLib = engine.evaluate(listOf<VersionedIdentifier?>(LIB_1), ALL_EXPRESSIONS)

        EvaluatedResourceTestUtils.assertEntireEvaluationResult(
            resultsSingleLib,
            LIB_1,
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
        )
        engine.state.clearEvaluatedResources()

        // Using the same engine, evaluate three libraries, two of which are not cached
        val resultsMultiLib: EvaluationResultsForMultiLib =
            engine.evaluate(ALL_LIB_IDS, ALL_EXPRESSIONS)

        EvaluatedResourceTestUtils.assertEntireEvaluationResult(
            resultsMultiLib,
            LIB_1,
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
        )

        EvaluatedResourceTestUtils.assertEntireEvaluationResult(
            resultsMultiLib,
            LIB_2,
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.PROCEDURE,
                        EvaluatedResourceTestUtils.CONDITION,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.PROCEDURE,
                        EvaluatedResourceTestUtils.CONDITION,
                    ),
                ENCOUNTER_EXPRESSION to listOf(),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
        )

        EvaluatedResourceTestUtils.assertEntireEvaluationResult(
            resultsMultiLib,
            LIB_3,
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(),
            ),
        )

        // Now use the same engine, but pass the identifiers in a different order
        val resultsMultiLibDifferentOrder =
            engine.evaluate(listOf(LIB_3, LIB_2, LIB_1), ALL_EXPRESSIONS)

        EvaluatedResourceTestUtils.assertEntireEvaluationResult(
            resultsMultiLibDifferentOrder,
            LIB_1,
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
        )

        EvaluatedResourceTestUtils.assertEntireEvaluationResult(
            resultsMultiLibDifferentOrder,
            LIB_2,
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.PROCEDURE,
                        EvaluatedResourceTestUtils.CONDITION,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.PROCEDURE,
                        EvaluatedResourceTestUtils.CONDITION,
                    ),
                ENCOUNTER_EXPRESSION to listOf(),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
        )

        EvaluatedResourceTestUtils.assertEntireEvaluationResult(
            resultsMultiLibDifferentOrder,
            LIB_3,
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(EvaluatedResourceTestUtils.CONDITION),
            ),
            mapOf(
                UNION_EXPRESSION to
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                ENCOUNTER_EXPRESSION to listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                CONDITION_EXPRESSION to listOf(),
            ),
        )
    }

    private fun getCqlEngineForFhirNewLibMgr(expressionCaching: Boolean): CqlEngine {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
            engineWithNewLibraryManager,
            expressionCaching,
            r4ModelResolver,
            EvaluatedResourceTestUtils.RETRIEVE_PROVIDER,
        )
    }

    companion object {
        private val LIB_1: VersionedIdentifier =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibLinearDepsTest1")
        private val LIB_2: VersionedIdentifier =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibLinearDepsTest2")
        private val LIB_3: VersionedIdentifier =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibLinearDepsTest3")
        private val LIB_WARNING_HIDING: VersionedIdentifier =
            EvaluatedResourceTestUtils.forId(
                "EvaluatedResourcesMultiLibLinearDepsTestWarningHiding"
            )
        private val LIB_ERROR_INVALID_CAST_EXPRESSION: VersionedIdentifier =
            EvaluatedResourceTestUtils.forId(
                "EvaluatedResourcesMultiLibLinearDepsTestErrorInvalidCastExpression"
            )

        private val ALL_LIB_IDS = listOf(LIB_1, LIB_2, LIB_3)

        private const val UNION_EXPRESSION = "Union"
        private val ENCOUNTER_EXPRESSION = ResourceType.Encounter.name
        private val CONDITION_EXPRESSION = ResourceType.Condition.name

        private val ALL_EXPRESSIONS: Set<String> =
            setOf(UNION_EXPRESSION, ENCOUNTER_EXPRESSION, CONDITION_EXPRESSION)

        @JvmStatic
        private fun singleLibParams(): List<Arguments> {
            return listOf(
                Arguments.of(
                    LIB_1,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    true,
                ),
                Arguments.of(
                    LIB_1,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    true,
                ),
                Arguments.of(
                    LIB_1,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    true,
                ),
                Arguments.of(
                    LIB_1,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    false,
                ),
                Arguments.of(
                    LIB_1,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    false,
                ),
                Arguments.of(
                    LIB_1,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    false,
                ),
                Arguments.of(
                    LIB_2,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    true,
                ),
                Arguments.of(
                    LIB_2,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    mutableListOf<Any?>(),
                    true,
                ),
                Arguments.of(
                    LIB_2,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    true,
                ),
                Arguments.of(
                    LIB_2,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    false,
                ),
                Arguments.of(
                    LIB_2,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    mutableListOf<Any?>(),
                    false,
                ),
                Arguments.of(
                    LIB_2,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    false,
                ),
                Arguments.of(
                    LIB_3,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                    true,
                ),
                Arguments.of(
                    LIB_3,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    true,
                ),
                Arguments.of(
                    LIB_3,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    mutableListOf<Any?>(),
                    true,
                ),
                Arguments.of(
                    LIB_3,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                    false,
                ),
                Arguments.of(
                    LIB_3,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    false,
                ),
                Arguments.of(
                    LIB_3,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    mutableListOf<Any?>(),
                    false,
                ),
            )
        }

        @JvmStatic
        private fun multiLibParams(): List<Arguments> {
            return listOf(
                Arguments.of(
                    LIB_1,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    true,
                ),
                Arguments.of(
                    LIB_1,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    true,
                ),
                Arguments.of(
                    LIB_1,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    true,
                ),
                Arguments.of(
                    LIB_1,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    false,
                ),
                Arguments.of(
                    LIB_1,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    false,
                ),
                Arguments.of(
                    LIB_1,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    false,
                ),
                Arguments.of(
                    LIB_2,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    true,
                ),
                Arguments.of(
                    LIB_2,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    mutableListOf<Any?>(),
                    true,
                ),
                Arguments.of(
                    LIB_2,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    true,
                ),
                Arguments.of(
                    LIB_2,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.CONDITION,
                        EvaluatedResourceTestUtils.ENCOUNTER,
                    ),
                    false,
                ),
                Arguments.of(
                    LIB_2,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    mutableListOf<Any?>(),
                    false,
                ),
                Arguments.of(
                    LIB_2,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    false,
                ),
                Arguments.of(
                    LIB_3,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                    true,
                ),
                Arguments.of(
                    LIB_3,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    true,
                ),
                Arguments.of(
                    LIB_3,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    mutableListOf<Any?>(),
                    true,
                ),
                Arguments.of(
                    LIB_3,
                    UNION_EXPRESSION,
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                    listOf(
                        EvaluatedResourceTestUtils.ENCOUNTER,
                        EvaluatedResourceTestUtils.PROCEDURE,
                    ),
                    false,
                ),
                Arguments.of(
                    LIB_3,
                    ENCOUNTER_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    listOf(EvaluatedResourceTestUtils.ENCOUNTER),
                    false,
                ),
                Arguments.of(
                    LIB_3,
                    CONDITION_EXPRESSION,
                    listOf(EvaluatedResourceTestUtils.CONDITION),
                    mutableListOf<Any?>(),
                    false,
                ),
            )
        }

        @JvmStatic
        private fun multiLibEnsurePartialCacheAllowsUncachedLibsToBeCompiledParams():
            List<Arguments> {
            return listOf(Arguments.of(true), Arguments.of(false))
        }
    }
}
