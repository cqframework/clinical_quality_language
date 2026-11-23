package org.opencds.cqf.cql.engine.fhir.data

import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.measureTimedValue
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Period
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Value
import org.slf4j.LoggerFactory

/**
 * Times FHIR joins with each combination of [CqlEngine.Options.EnableQueryOptimization] and
 * [CqlEngine.Options.EnableFunctionMemoization], and checks that they all agree.
 */
internal class QueryJoinBenchmark : FhirExecutionTestBase() {
    private val encounters =
        (0 until COUNT).map { i ->
            r4ModelResolver!!.toCqlValue(
                Encounter()
                    .setStatus(Encounter.EncounterStatus.FINISHED)
                    .setPeriod(
                        Period()
                            .setStartElement(DateTimeType("2020-01-01T00:00:00Z").plusDays(i))
                            .setEndElement(DateTimeType("2020-01-01T12:00:00Z").plusDays(i))
                    )
                    .setId("E$i")
            )
        }

    // All in 2019, so no condition overlaps an encounter; only the last one is active
    private val conditions =
        (0 until COUNT).map { i ->
            r4ModelResolver!!.toCqlValue(
                Condition()
                    .setClinicalStatus(
                        CodeableConcept(
                            Coding(
                                "http://terminology.hl7.org/CodeSystem/condition-clinical",
                                if (i == COUNT - 1) "active" else "resolved",
                                null,
                            )
                        )
                    )
                    .setOnset(DateTimeType("2019-01-01T00:00:00Z").plusDays(i))
                    .setAbatement(DateTimeType("2019-01-02T00:00:00Z").plusDays(i))
                    .setId("C$i")
            )
        }

    private var retrieves = 0

    private val retrieveProvider =
        object : RetrieveProvider {
            override fun retrieve(
                context: String?,
                contextPath: String?,
                contextValue: String?,
                dataType: String,
                templateId: String?,
                codePath: String?,
                codes: Iterable<Code>?,
                valueSet: String?,
                datePath: String?,
                dateLowPath: String?,
                dateHighPath: String?,
                dateRange: Interval?,
            ): Iterable<Value?> {
                retrieves++
                return when (dataType) {
                    "Encounter" -> encounters
                    "Condition" -> conditions
                    else -> emptyList()
                }
            }
        }

    private enum class Mode(val options: Set<CqlEngine.Options>) {
        NAIVE(emptySet()),
        MEMOIZATION(setOf(CqlEngine.Options.EnableFunctionMemoization)),
        OPTIMIZATION(setOf(CqlEngine.Options.EnableQueryOptimization)),
        BOTH(
            setOf(
                CqlEngine.Options.EnableFunctionMemoization,
                CqlEngine.Options.EnableQueryOptimization,
            )
        ),
    }

    private class Run(val value: Value?, val time: Duration, val retrieves: Int)

    private fun run(mode: Mode, expression: String): Run {
        val engine =
            CqlEngine(
                environment,
                (mode.options + CqlEngine.Options.EnableExpressionCaching).toMutableSet(),
            )
        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, retrieveProvider),
        )
        retrieves = 0
        val (value, time) =
            measureTimedValue {
                engine
                    .evaluate { library("QueryJoinBenchmark") { expressions(expression) } }
                    .onlyResultOrThrow[expression]!!
                    .value
            }
        return Run(value, time, retrieves)
    }

    @ParameterizedTest
    @ValueSource(
        strings =
            [
                "Without, expression source",
                "Without, retrieve source",
                "With, row and related conjuncts",
                "Cross product",
            ]
    )
    fun allModesAgree(expression: String) {
        // Warm up the JVM so the timings below are comparable
        repeat(WARM_UP_ITERATIONS) { Mode.entries.forEach { run(it, expression) } }

        val runs =
            Mode.entries.associateWith { mode -> (1..ITERATIONS).map { run(mode, expression) } }
        val expected = runs.getValue(Mode.NAIVE).first().value
        runs.values.flatten().forEach { assertEquals(expected, it.value) }

        logger.info(
            "{} ({} x {}): {}",
            expression,
            COUNT,
            COUNT,
            runs.entries.joinToString { (mode, modeRuns) ->
                val median = modeRuns.map { it.time }.sorted()[modeRuns.size / 2]
                "$mode ${median.inWholeMilliseconds} ms, ${modeRuns.first().retrieves} retrieves"
            },
        )
    }

    companion object {
        private const val COUNT = 200
        private const val WARM_UP_ITERATIONS = 3
        private const val ITERATIONS = 5
        private val logger = LoggerFactory.getLogger(QueryJoinBenchmark::class.java)

        private fun DateTimeType.plusDays(days: Int): DateTimeType =
            copy().apply { add(java.util.Calendar.DATE, days) }
    }
}
