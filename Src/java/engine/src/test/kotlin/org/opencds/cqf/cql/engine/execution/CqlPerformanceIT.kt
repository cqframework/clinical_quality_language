package org.opencds.cqf.cql.engine.execution

import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*
import java.util.stream.Stream
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.fhir.ucum.UcumException
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CqlPerformanceIT : CqlTestBase() {
    // This test is a basically empty library that tests how long the engine
    // initialization takes.
    @Test
    @Throws(IOException::class, UcumException::class)
    fun engineInit() {
        val libraryId = toElmIdentifier("Test")
        runPerformanceTest(libraryId, 1.0, null, defaultCompilerOptions(), defaultEngineOptions())
    }

    // This test is for the various CQL operators
    @ParameterizedTest
    @MethodSource("engineOptionCombinations")
    @Throws(IOException::class, UcumException::class)
    fun mainSuite(engineOptions: MutableSet<CqlEngine.Options>) {
        val libraryId = toElmIdentifier("CqlPerformanceTest", "1")
        val date = ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId())
        runPerformanceTest(libraryId, 1000.0, date, defaultCompilerOptions(), engineOptions)
    }

    // This test is for the runtime errors
    // @Test
    // TODO: Ratio type not implemented error
    @Throws(IOException::class, UcumException::class)
    fun testErrorSuite() {
        // Map<VersionedIdentifier, Library> map = new HashMap<>();
        // VersionedIdentifier libraryId = toElmIdentifier("CqlErrorTestSuite");
        // map.put(libraryId, getLibrary(libraryId));
        // runPerformanceTest(libraryId, map, 10.0, null);
    }

    // This test is to check the validity of the internal representation of the CQL
    // types (OPTIONAL)
    @Test
    @Throws(IOException::class, UcumException::class)
    fun internalTypeRepresentationSuite() {
        val libraryId = toElmIdentifier("CqlInternalTypeRepresentationSuite", "1")
        runPerformanceTest(libraryId, 10.0, null, defaultCompilerOptions(), defaultEngineOptions())
    }

    private fun runPerformanceTest(
        libraryId: VersionedIdentifier,
        maxPerIterationMs: Double,
        evaluationZonedDateTime: ZonedDateTime?,
        compilerOptions: CqlCompilerOptions,
        engineOptions: MutableSet<CqlEngine.Options>,
    ) {
        // A new CqlEngine is created for each loop because it resets and rebuilds the
        // context completely.

        val environment = Environment(getLibraryManager(compilerOptions))

        // Warm up the JVM
        for (i in 0..<ITERATIONS) {
            val engine = CqlEngine(environment, engineOptions)
            engine
                .evaluate {
                    library(libraryId)
                    evaluationDateTime = evaluationZonedDateTime
                }
                .onlyResultOrThrow
        }

        val start = Instant.now()
        for (i in 0..<ITERATIONS) {
            val engine = CqlEngine(environment, engineOptions)
            engine
                .evaluate {
                    library(libraryId)
                    evaluationDateTime = evaluationZonedDateTime
                }
                .onlyResultOrThrow
        }
        val finish = Instant.now()

        val timeElapsed = Duration.between(start, finish).toMillis()
        val perIteration = timeElapsed.toDouble() / ITERATIONS.toDouble()

        logger.info(
            "{} performance test took {} millis for {} iterations. Per iteration: {} ms",
            libraryId.id,
            timeElapsed,
            ITERATIONS,
            perIteration,
        )
        Assertions.assertTrue(
            perIteration < maxPerIterationMs,
            String.format(
                "%s took longer per iteration than allowed. max: %3.2f, actual: %3.2f",
                libraryId.id,
                maxPerIterationMs,
                perIteration,
            ),
        )
    }

    private fun defaultCompilerOptions(): CqlCompilerOptions {
        val options = defaultOptions()
        // This test suite contains some definitions that use features that are usually
        // turned off for CQL.
        options.options.remove(CqlCompilerOptions.Options.DisableListDemotion)
        options.options.remove(CqlCompilerOptions.Options.DisableListPromotion)

        // When called with the null argument, the toString function in the CqlPerformanceTest
        // library can only be unambiguously resolved at runtime if the library is
        // compiled with signature level set to Overloads or All.
        options.withSignatureLevel(LibraryBuilder.SignatureLevel.Overloads)
        return options
    }

    companion object {
        private const val ITERATIONS = 200

        private val logger: Logger = LoggerFactory.getLogger(CqlPerformanceIT::class.java)

        private fun defaultEngineOptions(): MutableSet<CqlEngine.Options> {
            return mutableSetOf()
        }

        @JvmStatic
        private fun engineOptionCombinations(): Stream<MutableSet<CqlEngine.Options>> {
            return Stream.of(
                defaultEngineOptions(),
                mutableSetOf(CqlEngine.Options.EnableProfiling),
            )
        }
    }
}
