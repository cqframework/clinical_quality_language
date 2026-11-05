package org.opencds.cqf.cql.engine.fhir.data

import kotlin.test.assertEquals
import kotlin.time.measureTimedValue
import org.hl7.elm.r1.FunctionDef
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Period
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.Profile
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.slf4j.LoggerFactory

/**
 * Fluent functions on FHIR resources called from a `without ... such that` join, the shape that
 * makes measure logic call the same function once per pair of resources.
 */
internal class FunctionMemoizationBenchmark : FhirExecutionTestBase() {
    private val encounters =
        (0 until COUNT).map { i ->
            r4ModelResolver!!.toCqlValue(
                Encounter()
                    .setPeriod(
                        Period()
                            .setStartElement(DateTimeType("2020-01-01T00:00:00Z").plusDays(i))
                            .setEndElement(DateTimeType("2020-01-01T12:00:00Z").plusDays(i))
                    )
                    .setId("E$i")
            )
        }

    // All in 2019, so no condition overlaps an encounter
    private val conditions =
        (0 until COUNT).map { i ->
            r4ModelResolver!!.toCqlValue(
                Condition()
                    .setOnset(DateTimeType("2019-01-01T00:00:00Z").plusDays(i))
                    .setAbatement(DateTimeType("2019-01-02T00:00:00Z").plusDays(i))
                    .setId("C$i")
            )
        }

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
                return when (dataType) {
                    "Encounter" -> encounters
                    "Condition" -> conditions
                    else -> emptyList()
                }
            }
        }

    private fun newEngine(memoize: Boolean): CqlEngine {
        val options =
            mutableSetOf(
                CqlEngine.Options.EnableExpressionCaching,
                CqlEngine.Options.EnableProfiling,
            )
        if (memoize) {
            options.add(CqlEngine.Options.EnableFunctionMemoization)
        }
        val engine = CqlEngine(environment, options)
        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, retrieveProvider),
        )
        return engine
    }

    private fun CqlEngine.evaluate(): Value? =
        evaluate { library("FunctionMemoizationBenchmark") { expressions(EXPRESSION) } }
            .onlyResultOrThrow[EXPRESSION]!!
            .value

    /** Number of times the body of the named function was evaluated, across all call paths. */
    private fun CqlEngine.bodyEvaluations(functionName: String): Long {
        fun Profile.Node.sum(): Long =
            children.values.sumOf { byElement ->
                byElement!!.entries.sumOf { (element, node) ->
                    val own =
                        if (element is FunctionDef && element.name == functionName) node.misses
                        else 0L
                    own + node.sum()
                }
            }
        return state.debugResult!!.profile!!.tree.sum()
    }

    @Test
    fun functionsAreEvaluatedOncePerResource() {
        // Warm up the JVM so the timings below are comparable
        repeat(WARM_UP_ITERATIONS) {
            newEngine(memoize = false).evaluate()
            newEngine(memoize = true).evaluate()
        }

        val baseline = newEngine(memoize = false)
        val (expected, baselineTime) = measureTimedValue { baseline.evaluate() }
        assertEquals(COUNT, (expected as List).count())
        assertEquals(COUNT.toLong() * COUNT, baseline.bodyEvaluations("prevalence"))
        assertEquals(COUNT.toLong() * COUNT, baseline.bodyEvaluations("hospitalization"))

        val memoized = newEngine(memoize = true)
        val (actual, memoizedTime) = measureTimedValue { memoized.evaluate() }
        assertEquals(expected, actual)
        assertEquals(COUNT.toLong(), memoized.bodyEvaluations("prevalence"))
        assertEquals(COUNT.toLong(), memoized.bodyEvaluations("hospitalization"))

        logger.info(
            "{} encounters x {} conditions: {} without memoization, {} with",
            COUNT,
            COUNT,
            baselineTime,
            memoizedTime,
        )
    }

    companion object {
        private const val COUNT = 200
        private const val WARM_UP_ITERATIONS = 3
        private const val EXPRESSION = "Encounters Without Condition"
        private val logger = LoggerFactory.getLogger(FunctionMemoizationBenchmark::class.java)

        private fun DateTimeType.plusDays(days: Int): DateTimeType =
            copy().apply { add(java.util.Calendar.DATE, days) }
    }
}
