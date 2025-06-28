package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Stream;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.fhir.ucum.UcumException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CqlPerformanceIT extends CqlTestBase {

    private static final Integer ITERATIONS = 200;

    private static final Logger logger = LoggerFactory.getLogger(CqlPerformanceIT.class);

    // This test is a basically empty library that tests how long the engine
    // initialization takes.
    @Test
    void engineInit() throws IOException, UcumException {
        VersionedIdentifier libraryId = toElmIdentifier("Test");
        runPerformanceTest(libraryId, 1.0, null, defaultCompilerOptions(), defaultEngineOptions());
    }

    // This test is for the various CQL operators
    @ParameterizedTest
    @MethodSource("engineOptionCombinations")
    void mainSuite(Set<CqlEngine.Options> engineOptions) throws IOException, UcumException {
        VersionedIdentifier libraryId = toElmIdentifier("CqlPerformanceTest", "1");
        ZonedDateTime date =
                ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId());
        runPerformanceTest(libraryId, 1000.0, date, defaultCompilerOptions(), engineOptions);
    }

    // This test is for the runtime errors
    // @Test
    // TODO: Ratio type not implemented error
    public void testErrorSuite() throws IOException, UcumException {
        // Map<VersionedIdentifier, Library> map = new HashMap<>();
        // VersionedIdentifier libraryId = toElmIdentifier("CqlErrorTestSuite");
        // map.put(libraryId, getLibrary(libraryId));
        // runPerformanceTest(libraryId, map, 10.0, null);
    }

    // This test is to check the validity of the internal representation of the CQL
    // types (OPTIONAL)
    @Test
    void internalTypeRepresentationSuite() throws IOException, UcumException {
        VersionedIdentifier libraryId = toElmIdentifier("CqlInternalTypeRepresentationSuite", "1");
        runPerformanceTest(libraryId, 10.0, null, defaultCompilerOptions(), defaultEngineOptions());
    }

    private void runPerformanceTest(
            VersionedIdentifier libraryId,
            Double maxPerIterationMs,
            ZonedDateTime evaluationZonedDateTime,
            CqlCompilerOptions compilerOptions,
            Set<CqlEngine.Options> engineOptions) {
        // A new CqlEngine is created for each loop because it resets and rebuilds the
        // context completely.

        Environment environment = new Environment(getLibraryManager(compilerOptions));

        // Warm up the JVM
        for (int i = 0; i < ITERATIONS; i++) {
            CqlEngine engine = new CqlEngine(environment, engineOptions);
            var results = engine.evaluate(libraryId, null, null, null, null, evaluationZonedDateTime);
        }

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            CqlEngine engine = new CqlEngine(environment, engineOptions);
            var results = engine.evaluate(libraryId, null, null, null, null, evaluationZonedDateTime);
        }
        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        Double perIteration = (double) timeElapsed / (double) ITERATIONS;

        logger.info(
                "{} performance test took {} millis for {} iterations. Per iteration: {} ms",
                libraryId.getId(),
                timeElapsed,
                ITERATIONS,
                perIteration);
        assertTrue(
                perIteration < maxPerIterationMs,
                String.format(
                        "%s took longer per iteration than allowed. max: %3.2f, actual: %3.2f",
                        libraryId.getId(), maxPerIterationMs, perIteration));
    }

    protected CqlCompilerOptions defaultCompilerOptions() {
        var options = CqlCompilerOptions.defaultOptions();
        // This test suite contains some definitions that use features that are usually
        // turned off for CQL.
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListDemotion);
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListPromotion);

        // When called with the null argument, the toString function in the CqlPerformanceTest
        // library can only be unambiguously resolved at runtime if the library is
        // compiled with signature level set to Overloads or All.
        options.withSignatureLevel(LibraryBuilder.SignatureLevel.Overloads);
        return options;
    }

    protected static Set<CqlEngine.Options> defaultEngineOptions() {
        return Set.of();
    }

    private static Stream<Set<CqlEngine.Options>> engineOptionCombinations() {
        return Stream.of(defaultEngineOptions(), Set.of(CqlEngine.Options.EnableProfiling));
    }
}
