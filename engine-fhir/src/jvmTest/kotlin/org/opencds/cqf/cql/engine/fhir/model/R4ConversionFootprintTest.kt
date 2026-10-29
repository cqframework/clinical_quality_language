package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.lang.ref.WeakReference
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.UriType
import org.junit.jupiter.api.Tag
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.fhir.data.MEMORY_BENCHMARK
import org.opencds.cqf.cql.engine.fhir.data.SyntheticChart
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.List as CqlList
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean
import org.openjdk.jol.info.GraphLayout

/**
 * Measures the heap footprint of converting HAPI R4 resources to CQL values with
 * [FhirModelResolver.toCqlValue], and checks that the converted values don't keep the source HAPI
 * objects alive.
 */
internal class R4ConversionFootprintTest {
    private val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

    @Test
    @Tag(MEMORY_BENCHMARK)
    fun singleResourceFootprint() {
        val generator = SyntheticChart(Random(42))
        val samples =
            listOf(
                generator.observation(0),
                generator.condition(0),
                generator.encounter(0),
                generator.medicationRequest(0),
            )

        println("%-18s %10s %10s %10s %7s".format("type", "hapi", "cql", "cql-only", "ratio"))
        for (resource in samples) {
            val converted = resolver.toCqlValue(resource)!!
            val hapiLayout = GraphLayout.parseInstance(resource)
            val cqlLayout = GraphLayout.parseInstance(converted)
            val cqlOnly = cqlLayout.subtract(hapiLayout).totalSize()
            println(
                "%-18s %10d %10d %10d %6.1fx"
                    .format(
                        resource.fhirType(),
                        hapiLayout.totalSize(),
                        cqlLayout.totalSize(),
                        cqlOnly,
                        cqlLayout.totalSize().toDouble() / hapiLayout.totalSize(),
                    )
            )
        }

        val observation = resolver.toCqlValue(samples[0])!!
        println(GraphLayout.parseInstance(observation).toFootprint())
        val (entries, nulls) = countEntries(observation)
        println("Observation map entries: $entries total, $nulls null")
    }

    private fun countEntries(value: Value?): Pair<Int, Int> =
        when (value) {
            is ClassInstance ->
                value.elements.values.fold(
                    value.elements.size to value.elements.values.count { it == null }
                ) { acc, child ->
                    val (e, n) = countEntries(child)
                    acc.first + e to acc.second + n
                }
            is CqlList ->
                value.fold(0 to 0) { acc, child ->
                    val (e, n) = countEntries(child)
                    acc.first + e to acc.second + n
                }
            else -> 0 to 0
        }

    @Test
    fun sparseAndDenseInstancesCompareEqual() {
        val sparse = resolver.toCqlValue(Coding("http://loinc.org", "8480-6", null))!!
        // The pre-sparse layout: every child present, unset ones null
        val dense =
            ClassInstance(
                sparse.type,
                mutableMapOf(
                    "id" to null,
                    "extension" to null,
                    "system" to resolver.toCqlValue(UriType("http://loinc.org")),
                    "version" to null,
                    "code" to resolver.toCqlValue(CodeType("8480-6")),
                    "display" to null,
                    "userSelected" to null,
                ),
            )

        assertEquals(setOf("system", "code"), sparse.elements.keys)
        assertEquals(true, EqualEvaluator.equal(sparse, dense)?.value)
        assertEquals(true, EqualEvaluator.equal(dense, sparse)?.value)
        assertTrue(elementsEquivalent(sparse, dense))
        assertEquals(dense, sparse)
        assertEquals(dense.hashCode(), sparse.hashCode())

        dense.elements["display"] = resolver.toCqlValue(StringType("Systolic"))
        assertNull(EqualEvaluator.equal(sparse, dense))
        assertFalse(elementsEquivalent(sparse, dense))
        assertNotEquals<Any>(dense, sparse)
    }

    // Full FHIR equivalence needs engine state; element-wise Kotlin equality is enough to exercise
    // the absent-is-null handling.
    private fun elementsEquivalent(left: ClassInstance, right: ClassInstance) =
        EquivalentEvaluator.classInstanceElementsEquivalent(left.elements, right.elements) { l, r ->
                (l == r).toCqlBoolean()
            }
            .value

    @Test
    fun convertedValuesDoNotReferenceHapiObjects() {
        val generator = SyntheticChart(Random(42))
        for (resource in generator.resources(200)) {
            val converted = resolver.toCqlValue(resource)!!
            val hapiClasses =
                GraphLayout.parseInstance(converted).classes.filter {
                    IBase::class.java.isAssignableFrom(it)
                }
            assertEquals(
                emptyList(),
                hapiClasses,
                "Converted ${resource.fhirType()} references HAPI objects",
            )
        }
    }

    @Test
    fun hapiResourcesAreCollectableAfterConversion() {
        val (converted, refs) = convertAndDropSources(SyntheticChart(Random(42)), 2_000)

        repeat(10) {
            if (refs.all { it.get() == null }) return@repeat
            System.gc()
            Thread.sleep(50)
        }

        val retained = refs.count { it.get() != null }
        assertEquals(0, retained, "$retained of ${refs.size} HAPI resources still reachable")
        assertEquals(refs.size, converted.size)
    }

    @Test
    @Tag(MEMORY_BENCHMARK)
    fun chartFootprint() {
        val generator = SyntheticChart(Random(42))
        for (size in listOf(1_000, 5_000)) {
            val chart = generator.resources(size)
            val hapiBytes = GraphLayout.parseInstance(chart).totalSize()
            val converted = chart.map { resolver.toCqlValue(it) }
            val cqlBytes = GraphLayout.parseInstance(converted).totalSize()
            println(
                "%6d resources: hapi %,d bytes (%,d/res), cql %,d bytes (%,d/res), %.1fx"
                    .format(
                        size,
                        hapiBytes,
                        hapiBytes / size,
                        cqlBytes,
                        cqlBytes / size,
                        cqlBytes.toDouble() / hapiBytes,
                    )
            )
            assertTrue(cqlBytes > 0)
        }
    }

    private fun convertAndDropSources(
        generator: SyntheticChart,
        size: Int,
    ): Pair<List<Any?>, List<WeakReference<Resource>>> {
        val chart = generator.resources(size)
        val refs = chart.map { WeakReference(it) }
        val converted = chart.map { resolver.toCqlValue(it) }
        return converted to refs
    }
}
