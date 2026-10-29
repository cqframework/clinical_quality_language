package org.opencds.cqf.cql.engine.fhir.data

import com.sun.management.GarbageCollectionNotificationInfo
import com.sun.management.HotSpotDiagnosticMXBean
import java.io.File
import java.lang.management.ManagementFactory
import java.util.concurrent.atomic.AtomicLong
import javax.management.NotificationEmitter
import javax.management.NotificationListener
import javax.management.openmbean.CompositeData
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.junit.jupiter.api.Tag
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.EvaluationResults
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.openjdk.jol.info.GraphLayout

const val MEMORY_BENCHMARK = "memory-benchmark"

/**
 * Measures the memory used to evaluate measure-style CQL over a single patient with a large chart.
 *
 * Run with `./gradlew :engine-fhir:memoryBenchmark`. Optional Gradle properties:
 * - `memoryBenchmark.sizes`: comma separated chart sizes (default `1000,5000,20000`)
 * - `memoryBenchmark.expressionCaching`: `on`, `off` or `both` (default `both`)
 * - `memoryBenchmark.attribute`: break the retained results down by expression (slow; use small
 *   sizes)
 * - `memoryBenchmark.heapDumpDir`: write a heap dump after each evaluation, with the engine and its
 *   results still reachable
 *
 * For each run, the HAPI chart is built first and counted in the baseline, so the reported numbers
 * are what evaluation adds on top of the source data:
 * - allocated: bytes allocated by the evaluating thread
 * - peak: the highest heap use after any GC during evaluation, minus the baseline
 * - retained: heap still used after a full GC, with the engine and results reachable, and with only
 *   the results reachable
 */
@Tag(MEMORY_BENCHMARK)
internal class LargeChartMemoryBenchmark : FhirExecutionTestBase() {
    @Suppress("LongParameterList")
    private class Measurement(
        val size: Int,
        val expressionCaching: Boolean,
        val hapiBytes: Long,
        val evaluationMillis: Long,
        val allocatedBytes: Long,
        val gcCount: Int,
        val peakBytes: Long?,
        val retainedWithEngineBytes: Long,
        val retainedResultsBytes: Long,
    )

    // Fields rather than locals, so the JIT can't treat them as dead before they're measured
    private var heldEngine: CqlEngine? = null
    private var heldResult: EvaluationResults? = null

    @Test
    fun largeChart() {
        val sizes =
            System.getProperty("memoryBenchmark.sizes", "1000,5000,20000").split(",").map {
                it.trim().toInt()
            }
        val cachingModes =
            when (System.getProperty("memoryBenchmark.expressionCaching", "both")) {
                "on" -> listOf(true)
                "off" -> listOf(false)
                else -> listOf(true, false)
            }
        val heapDumpDir = System.getProperty("memoryBenchmark.heapDumpDir")?.let(::File)
        val attribute = System.getProperty("memoryBenchmark.attribute").toBoolean()

        // Load classes and warm up the JIT so the first measured run isn't an outlier
        repeat(WARM_UP_ITERATIONS) {
            measure(WARM_UP_SIZE, expressionCaching = true, heapDumpDir = null, attribute = false)
        }

        val measurements =
            sizes.flatMap { size ->
                cachingModes.map { caching -> measure(size, caching, heapDumpDir, attribute) }
            }

        report(measurements)
    }

    private fun measure(
        size: Int,
        expressionCaching: Boolean,
        heapDumpDir: File?,
        attribute: Boolean,
    ): Measurement {
        val beforeChart = usedHeapAfterGc()
        val chart = SyntheticChart(Random(SEED))
        val patient = chart.patient()
        val resources = chart.resources(size)
        val baseline = usedHeapAfterGc()

        heldEngine = newEngine(expressionCaching, patient, resources)
        val gcMonitor = GcMonitor()
        val allocatedBefore = threadAllocatedBytes()
        val start = System.nanoTime()

        heldResult = heldEngine!!.evaluate { library(LIBRARY) }

        val evaluationMillis = (System.nanoTime() - start) / NANOS_PER_MILLI
        val allocatedBytes = threadAllocatedBytes() - allocatedBefore
        gcMonitor.close()

        checkResults(heldResult!!, resources)

        val retainedWithEngine = usedHeapAfterGc() - baseline
        heapDumpDir?.let {
            it.mkdirs()
            dumpHeap(
                File(
                    it,
                    "large-chart-$size-caching-${if (expressionCaching) "on" else "off"}.hprof",
                )
            )
        }
        heldEngine = null
        val retainedResults = usedHeapAfterGc() - baseline
        if (attribute) {
            attribute(heldResult!!, size, expressionCaching)
        }
        heldResult = null

        // Keep the chart reachable until the measurements above are done
        check(resources.size == size)

        return Measurement(
            size = size,
            expressionCaching = expressionCaching,
            hapiBytes = baseline - beforeChart,
            evaluationMillis = evaluationMillis,
            allocatedBytes = allocatedBytes,
            gcCount = gcMonitor.count,
            peakBytes = gcMonitor.peakUsedAfterGc?.let { it - baseline },
            retainedWithEngineBytes = retainedWithEngine,
            retainedResultsBytes = retainedResults,
        )
    }

    private fun newEngine(
        expressionCaching: Boolean,
        patient: Patient,
        resources: List<Resource>,
    ): CqlEngine {
        val options =
            if (expressionCaching) mutableSetOf(CqlEngine.Options.EnableExpressionCaching)
            else mutableSetOf()
        val engine = CqlEngine(environment, options)
        engine.state.environment.registerDataProvider(
            fhirModelNamespaceUri,
            CompositeDataProvider(r4ModelResolver, ChartRetrieveProvider(patient, resources)),
        )
        return engine
    }

    /** Sanity checks so the benchmark measures logic that actually selects data. */
    private fun checkResults(result: EvaluationResults, resources: List<Resource>) {
        val values = result.onlyResultOrThrow
        assertEquals(
            resources.count { it is Observation }.toCqlInteger(),
            values["Observation Count"]!!.value,
        )
        assertTrue(
            (values["Diabetic Encounters In Period"]!!.value as Iterable<*>).any(),
            "Expected diabetic encounters in the measurement period",
        )
    }

    private fun report(measurements: List<Measurement>) {
        val header =
            listOf(
                "size",
                "caching",
                "hapi MB",
                "eval ms",
                "allocated MB",
                "GCs",
                "peak MB",
                "retained MB",
                "results MB",
                "retained KB/res",
            )
        val rows =
            measurements.map {
                listOf(
                    it.size.toString(),
                    onOff(it.expressionCaching),
                    mb(it.hapiBytes),
                    it.evaluationMillis.toString(),
                    mb(it.allocatedBytes),
                    it.gcCount.toString(),
                    it.peakBytes?.let(::mb) ?: "n/a",
                    mb(it.retainedWithEngineBytes),
                    mb(it.retainedResultsBytes),
                    "%.1f".format(it.retainedWithEngineBytes / KB / it.size),
                )
            }

        printTable("Large chart memory benchmark", header, rows)

        System.getProperty("memoryBenchmark.reportDir")?.let {
            val dir = File(it).apply { mkdirs() }
            File(dir, "large-chart.csv")
                .writeText(
                    (listOf(header) + rows).joinToString("\n", postfix = "\n") { row ->
                        row.joinToString(",")
                    }
                )
        }
    }

    private fun printTable(title: String, header: List<String>, rows: List<List<String>>) {
        val widths =
            header.indices.map { i -> (rows.map { it[i] } + header[i]).maxOf { it.length } }
        fun line(cells: List<String>) =
            cells.mapIndexed { i, cell -> cell.padStart(widths[i]) }.joinToString("  ")
        println(
            (listOf(line(header), widths.joinToString("  ") { "-".repeat(it) }) + rows.map(::line))
                .joinToString("\n", prefix = "\n$title\n", postfix = "\n")
        )
    }

    /**
     * Breaks the retained results down by expression, with JOL: the size of each value and of its
     * evaluated resources, and how many evaluated resources there are versus distinct resource ids.
     * Sizes include objects shared with other expressions; the total counts them once. Slow, so
     * best used with small charts.
     */
    private fun attribute(results: EvaluationResults, size: Int, expressionCaching: Boolean) {
        val expressionResults = results.onlyResultOrThrow.expressionResults
        val header = listOf("expression", "value KB", "resources KB", "resources", "distinct ids")
        val rows =
            expressionResults.map { (name, result) ->
                val resources = result.evaluatedResources.orEmpty()
                listOf(
                    name,
                    kb(graphSize(result.value)),
                    kb(graphSize(resources)),
                    resources.size.toString(),
                    resources.mapNotNull { r4ModelResolver!!.resolveId(it) }.toSet().size.toString(),
                )
            }
        val allResources = expressionResults.values.flatMap { it.evaluatedResources.orEmpty() }
        val totals =
            listOf(
                "total (shared counted once)",
                kb(graphSize(expressionResults.values.map { it.value })),
                kb(graphSize(allResources)),
                allResources.size.toString(),
                allResources.mapNotNull { r4ModelResolver!!.resolveId(it) }.toSet().size.toString(),
            )
        printTable(
            "Retained results by expression: $size resources, caching ${onOff(expressionCaching)}",
            header,
            rows + listOf(totals),
        )
    }

    /** Serves the chart from memory, filtered by type and code, converting on every retrieve. */
    private inner class ChartRetrieveProvider(
        private val patient: Patient,
        private val resources: List<Resource>,
    ) : RetrieveProvider {
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
            val matching =
                if (dataType == "Patient") listOf(patient)
                else
                    resources.filter {
                        it.fhirType() == dataType && (codes == null || hasCode(it, codes))
                    }
            return matching.map { r4ModelResolver!!.toCqlValue(it) }
        }

        private fun hasCode(resource: Resource, codes: Iterable<Code>): Boolean {
            val concept: CodeableConcept =
                when (resource) {
                    is Observation -> resource.code
                    is Condition -> resource.code
                    else -> return false
                }
            return concept.coding.any { coding ->
                codes.any { it.system == coding.system && it.code == coding.code }
            }
        }
    }

    /** Records the highest heap use after GC, and the number of GCs, while open. */
    private class GcMonitor : AutoCloseable {
        private val peak = AtomicLong(-1)
        private val gcs = AtomicLong()
        private val emitters =
            ManagementFactory.getGarbageCollectorMXBeans().filterIsInstance<NotificationEmitter>()
        private val listener = NotificationListener { notification, _ ->
            if (
                notification.type ==
                    GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION
            ) {
                val info =
                    GarbageCollectionNotificationInfo.from(notification.userData as CompositeData)
                val used = info.gcInfo.memoryUsageAfterGc.values.sumOf { it.used }
                peak.accumulateAndGet(used, ::maxOf)
                gcs.incrementAndGet()
            }
        }

        init {
            emitters.forEach { it.addNotificationListener(listener, null, null) }
        }

        val count: Int
            get() = gcs.get().toInt()

        val peakUsedAfterGc: Long?
            get() = peak.get().takeIf { it >= 0 }

        override fun close() {
            emitters.forEach { it.removeNotificationListener(listener) }
        }
    }

    companion object {
        private const val LIBRARY = "LargeChartMemoryBenchmark"
        private const val SEED = 42
        private const val WARM_UP_ITERATIONS = 3
        private const val WARM_UP_SIZE = 500
        private const val NANOS_PER_MILLI = 1_000_000
        private const val KB = 1024.0
        private const val GC_SETTLE_ATTEMPTS = 10
        private const val GC_SETTLE_MILLIS = 50L
        private const val GC_SETTLE_TOLERANCE = 0.005

        private fun mb(bytes: Long) = "%.1f".format(bytes / KB / KB)

        private fun kb(bytes: Long) = "%.1f".format(bytes / KB)

        private fun onOff(enabled: Boolean) = if (enabled) "on" else "off"

        private fun graphSize(root: Any?): Long =
            if (root == null) 0 else GraphLayout.parseInstance(root).totalSize()

        private fun threadAllocatedBytes(): Long =
            (ManagementFactory.getThreadMXBean() as com.sun.management.ThreadMXBean)
                .getThreadAllocatedBytes(Thread.currentThread().id)

        /** Used heap after repeated full GCs, once it stops shrinking. */
        private fun usedHeapAfterGc(): Long {
            val memory = ManagementFactory.getMemoryMXBean()
            var previous = Long.MAX_VALUE
            repeat(GC_SETTLE_ATTEMPTS) {
                System.gc()
                Thread.sleep(GC_SETTLE_MILLIS)
                val used = memory.heapMemoryUsage.used
                if (previous - used < previous * GC_SETTLE_TOLERANCE) return used
                previous = used
            }
            return previous
        }

        private fun dumpHeap(file: File) {
            file.delete()
            ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean::class.java)
                .dumpHeap(file.absolutePath, true)
        }
    }
}
