package org.opencds.cqf.cql.engine.execution;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.TimeZone;

import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.fhir.ucum.UcumException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class CqlPerformanceIT  extends TranslatingTestBase {

    private static final Integer ITERATIONS = 200;

    private static final Logger logger = LoggerFactory.getLogger(CqlPerformanceIT.class);

    // This test is a basically empty library that tests how long the engine initialization takes.
    @Test
    public void testEngineInit() throws IOException, UcumException {
        Library library = this.toLibrary("library Test");
        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        runPerformanceTest("Engine init", "Test", libraryLoader, 0.2);
    }

    // This test is for the various CQL operators
    @Test
    public void testMainSuite() throws IOException, UcumException {
        Library library = translate("portable/CqlTestSuite.cql");
        ZonedDateTime date = ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId());
        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        runPerformanceTest("CqlTestSuite", "CqlTestSuite", libraryLoader, 350.0, date);
    }

    // This test is for the runtime errors
    // @Test
    // TODO: Ratio type not implemented error
    public void testErrorSuite() throws IOException, UcumException {
        Library library = translate("portable/CqlErrorTestSuite.cql");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        runPerformanceTest("CqlErrorTestSuite", "CqlErrorTestSuite", libraryLoader, 10.0);
    }

    // This test is to check the validity of the internal representation of the CQL types (OPTIONAL)
    @Test
    public void testInternalTypeRepresentationSuite() throws IOException, UcumException {
        Library library = translate("portable/CqlInternalTypeRepresentationSuite.cql");
        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));
        runPerformanceTest("CqlInternalTypeRepresentationSuite", "CqlInternalTypeRepresentationSuite", libraryLoader, 3.0);
    }

    private Library translate(String file) throws UcumException, IOException {
        return new TranslatorHelper().translate(file, LibraryBuilder.SignatureLevel.All);
    }

    private void runPerformanceTest(String testName, String libraryName, LibraryLoader libraryLoader, Double maxPerIterationMs) {
        runPerformanceTest(testName, libraryName, libraryLoader, maxPerIterationMs, null);
    }

    private void runPerformanceTest(String testName, String libraryName, LibraryLoader libraryLoader, Double maxPerIterationMs, ZonedDateTime evaluationZonedDateTime) {
        // A new CqlEngine is created for each loop because it resets and rebuilds the context completely.

        // Warm up the JVM
        for (int i = 0; i < ITERATIONS; i++) {
            CqlEngine engine = new CqlEngine(libraryLoader);
            engine.evaluate(new VersionedIdentifier().withId(libraryName), null, null,
                null, null, evaluationZonedDateTime);
        }

        Instant start = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            CqlEngine engine = new CqlEngine(libraryLoader);
            engine.evaluate(new VersionedIdentifier().withId(libraryName), null, null,
                null, null, evaluationZonedDateTime);
        }
        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        Double perIteration = (double)timeElapsed / (double)ITERATIONS;

        logger.info("{} performance test took {} millis for {} iterations. Per iteration: {} ms", testName, timeElapsed, ITERATIONS, perIteration);
        assertTrue(perIteration < maxPerIterationMs, String.format("%s took longer per iteration than allowed. max: %3.2f, actual: %3.2f", testName, maxPerIterationMs, perIteration));
    }
}
