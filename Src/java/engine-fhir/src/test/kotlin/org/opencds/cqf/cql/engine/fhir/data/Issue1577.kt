package org.opencds.cqf.cql.engine.fhir.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhirpath.TranslatorHelper
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.fhir.model.CachedR4FhirModelResolver
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

/**
 * Tests the implementation of [org.opencds.cqf.cql.engine.execution.Environment.is] and
 * [org.opencds.cqf.cql.engine.execution.Environment.as] for Java Iterables.
 */
class Issue1577 {
    @Test
    fun listTypeOperators() {
        val engine =
            TranslatorHelper.getEngine(
                """
                library Issue1577
                using FHIR version '4.0.1'
                context Patient
                define expr1: [Condition] is List<Any>
                define expr2: [Condition] union [Observation]
                """
                    .trimIndent()
            )

        val retrieveProvider =
            object : RetrieveProvider {
                override fun retrieve(
                    context: String?,
                    contextPath: String?,
                    contextValue: Any?,
                    dataType: String,
                    templateId: String?,
                    codePath: String?,
                    codes: Iterable<Code>?,
                    valueSet: String?,
                    datePath: String?,
                    dateLowPath: String?,
                    dateHighPath: String?,
                    dateRange: Interval?,
                ): Iterable<*> =
                    when (dataType) {
                        "Patient" -> listOf(Patient().setId("pat1"))
                        // Note: returning an Iterable implementation and not a list to test the
                        // handling of different Iterable types
                        "Condition" ->
                            object : Iterable<Any?> {
                                override fun iterator(): Iterator<*> {
                                    return listOf(Condition().setId("cond1")).iterator()
                                }
                            }
                        "Observation" ->
                            object : Iterable<Any?> {
                                override fun iterator(): Iterator<*> {
                                    return listOf(Observation().setId("obs1")).iterator()
                                }
                            }
                        else -> listOf<Any?>()
                    }
            }
        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(CachedR4FhirModelResolver(), retrieveProvider),
        )
        val evaluationResult = engine.evaluate { library("Issue1577") }.onlyResultOrThrow

        val expr1Result = evaluationResult["expr1"]!!.value
        assertIs<Boolean>(expr1Result)
        assertTrue(expr1Result)

        val expr2Result = evaluationResult["expr2"]!!.value
        assertIs<Iterable<*>>(expr2Result)
        assertEquals(2, expr2Result.toList().size)
    }
}
