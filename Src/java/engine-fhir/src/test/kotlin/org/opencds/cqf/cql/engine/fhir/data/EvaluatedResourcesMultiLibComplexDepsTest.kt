package org.opencds.cqf.cql.engine.fhir.data

import java.util.stream.Stream
import javax.annotation.Nonnull
import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.fhir.instance.model.api.IBaseResource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.EvaluationResult
import org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.Companion.allEncounters
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider

/**
 * See EvaluatedResourcesMultiLibComplexDepsTest.md for a mermaid diagram of the library
 * dependencies
 */
internal class EvaluatedResourcesMultiLibComplexDepsTest : FhirExecutionMultiLibTestBase() {
    @ParameterizedTest
    @MethodSource("singleLibraryEvaluationParams")
    fun singleLibraryEvaluation(
        expressionCaching: Boolean,
        libraryIdentifier: VersionedIdentifier,
        expressionName: String?,
        expectedEvaluatedResources: List<IBaseResource>,
        expectedValues: List<IBaseResource>,
    ) {
        val engine = getCqlEngineForFhirExistingLibMgr(expressionCaching)

        val resultsSingleLib: EvaluationResult? =
            engine.evaluate(libraryIdentifier, ALL_EXPRESSIONS)

        EvaluatedResourceTestUtils.assertEvaluationResult(
            resultsSingleLib,
            expressionName,
            expectedEvaluatedResources,
            expectedValues,
        )
    }

    @ParameterizedTest
    @MethodSource("multiLibrarySingleEvaluationAtATimeParams")
    fun multiLibrarySingleEvaluationAtATime(
        expressionCaching: Boolean,
        libraryIdentifier: VersionedIdentifier,
        expressionName: String?,
        expectedEvaluatedResources: List<IBaseResource>,
        expectedValues: List<IBaseResource>,
    ) {
        val engine = getCqlEngineForFhirExistingLibMgr(expressionCaching)

        val resultsSingleLib = engine.evaluate(listOf(libraryIdentifier), ALL_EXPRESSIONS)

        Assertions.assertTrue(resultsSingleLib.containsResultsFor(libraryIdentifier))
        Assertions.assertTrue(resultsSingleLib.containsResultsFor(libraryIdentifier))
        Assertions.assertFalse(resultsSingleLib.containsExceptionsFor(libraryIdentifier))
        Assertions.assertFalse(resultsSingleLib.containsExceptionsFor(libraryIdentifier))
        Assertions.assertNotNull(resultsSingleLib.getResultFor(libraryIdentifier))
        Assertions.assertNotNull(resultsSingleLib.onlyResultOrThrow)
        Assertions.assertNull(resultsSingleLib.getExceptionFor(libraryIdentifier))

        EvaluatedResourceTestUtils.assertEvaluationResult(
            resultsSingleLib,
            libraryIdentifier,
            expressionName,
            expectedEvaluatedResources,
            expectedValues,
        )
    }

    @ParameterizedTest
    @MethodSource("multiLibraryEvaluationParams")
    fun multiLibraryEvaluation(
        expressionCaching: Boolean,
        libraryIdentifier: VersionedIdentifier?,
        expressionName: String?,
        expectedEvaluatedResources: List<IBaseResource>,
        expectedValues: List<IBaseResource>,
    ) {
        val engine = getCqlEngineForFhirExistingLibMgr(expressionCaching)

        val allLibs = listOf(LIB_1A, LIB_1B)

        val results = engine.evaluate(allLibs, ALL_EXPRESSIONS)

        Assertions.assertTrue(results.containsResultsFor(LIB_1A))
        Assertions.assertTrue(results.containsResultsFor(LIB_1B))
        Assertions.assertFalse(results.containsExceptionsFor(LIB_1A))
        Assertions.assertFalse(results.containsExceptionsFor(LIB_1B))
        Assertions.assertNotNull(results.getResultFor(LIB_1A))
        Assertions.assertThrows(IllegalStateException::class.java) { results.onlyResultOrThrow }
        Assertions.assertNull(results.getExceptionFor(LIB_1A))

        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            libraryIdentifier,
            expressionName,
            expectedEvaluatedResources,
            expectedValues,
        )
    }

    @Nonnull
    private fun getCqlEngineForFhirExistingLibMgr(expressionCaching: Boolean): CqlEngine {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
            engineWithExistingLibraryManager,
            expressionCaching,
            r4ModelResolver,
            RETRIEVE_PROVIDER_COMPLEX,
        )
    }

    @Nonnull
    private fun getCqlEngineForFhirNewLibMgr(expressionCaching: Boolean): CqlEngine {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
            engineWithNewLibraryManager,
            expressionCaching,
            r4ModelResolver,
            RETRIEVE_PROVIDER_COMPLEX,
        )
    }

    companion object {
        private val RETRIEVE_PROVIDER_COMPLEX: RetrieveProvider =
            EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.INSTANCE

        private val LIB_1A: VersionedIdentifier =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibComplexDepsTest_Level1A")
        private val LIB_1B: VersionedIdentifier =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibComplexDepsTest_Level1B")

        private const val EXPRESSION_ENCOUNTERS_A = "Encounters A"
        private const val EXPRESSION_ENCOUNTERS_B = "Encounters B"
        private val ALL_EXPRESSIONS = setOf(EXPRESSION_ENCOUNTERS_A, EXPRESSION_ENCOUNTERS_B)

        @JvmStatic
        private fun singleLibraryEvaluationParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    true,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT3,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT3,
                    ),
                ),
                Arguments.of(
                    true,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    true,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    true,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT1
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT3,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT3,
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT1
                    ),
                ),
            )
        }

        @JvmStatic
        private fun multiLibrarySingleEvaluationAtATimeParams(): Stream<Arguments?> {
            return Stream.of<Arguments?>(
                Arguments.of(
                    true,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT3,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT3,
                    ),
                ),
                Arguments.of(
                    true,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    true,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT3,
                    ),
                ),
                Arguments.of(
                    true,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT1
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT3,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT3,
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT1,
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT1
                    ),
                ),
            )
        }

        @JvmStatic
        private fun multiLibraryEvaluationParams(): Stream<Arguments?> {
            return Stream.of<Arguments?>(
                Arguments.of(
                    true,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT3,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT3,
                    ),
                ),
                Arguments.of(
                    true,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    true,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    true,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT1
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT3,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT3,
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_A,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_ARRIVED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1A,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT1,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider
                            .ENCOUNTER_PLANNED_PAT2,
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2,
                    ),
                ),
                Arguments.of(
                    false,
                    LIB_1B,
                    EXPRESSION_ENCOUNTERS_B,
                    allEncounters,
                    listOf(
                        EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT1
                    ),
                ),
            )
        }
    }
}
