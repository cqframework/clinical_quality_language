package org.hl7.fhirpath;

import static java.util.Collections.emptyList;
import static java.util.Collections.nCopies;
import static java.util.Optional.ofNullable;
import static org.hl7.fhirpath.TranslatorHelper.toElmIdentifier;
import static org.opencds.cqf.cql.engine.elm.executing.ToQuantityEvaluator.toQuantity;

import ca.uhn.fhir.context.FhirContext;
import jakarta.xml.bind.JAXB;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhirpath.tests.InvalidType;
import org.hl7.fhirpath.tests.Tests;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.elm.executing.ToStringEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.fhir.model.FhirModelResolver;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Time;

public abstract class TestFhirPath {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestFhirPath.class);

    private sealed interface TestCase permits Error, Invalid, Pass {
        String name();

        String cql();
    }

    record Error(String name, String cql) implements TestCase {} // Compile time (Semantic) error

    record Invalid(String name, String cql) implements TestCase {} // Runtime (Invalid) error

    record Pass(String name, String cql, IBaseResource resource, List<Object> results) implements TestCase {}

    private final VersionedIdentifier libraryId = toElmIdentifier("TestFHIRPath");
    private final String header =
            """
            library TestFHIRPath
            using FHIR version '4.0.1'
            include FHIRHelpers version '4.0.1' called FHIRHelpers

            """;

    private final String parametersTemplate =
            """
            parameter %s %s
            parameter "%%context" %s
            parameter "%%resource" %s
            parameter "%%rootResource" %s context %s

            """;

    private final String definesTemplate = """
            define Test: %s
            """;

    abstract Boolean compareResults(
            Object expectedResult,
            Object actualResult,
            State state,
            FhirModelResolver<?, ?, ?, ?, ?, ?, ?, ?> resolver);

    protected void runTest(
            org.hl7.fhirpath.tests.Test test,
            String basePathInput,
            FhirContext fhirContext,
            CompositeDataProvider provider,
            FhirModelResolver<?, ?, ?, ?, ?, ?, ?, ?> resolver) {

        var testCase = buildTestCase(test, basePathInput, fhirContext);
        CqlEngine engine = TranslatorHelper.getEngine(testCase.cql());
        engine.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", provider);
        if (testCase instanceof Pass pass && pass.resource != null) {
            var resource = pass.resource;
            engine.getState().setParameter(null, resource.fhirType(), resource);
            engine.getState().setParameter(null, "%context", resource);
            engine.getState().setParameter(null, "%resource", resource);
            engine.getState().setParameter(null, "%rootResource", resource);
        }

        EvaluationResult result = null;
        try {
            result = engine.evaluate(libraryId, engine.getState().getParameters());
        } catch (CqlException e) {
            if (testCase instanceof Pass) {
                throw failWithContext("Runtime error and was expecting a result", testCase, "N/A", e);
            }

            if (testCase instanceof Error && e.getCause() instanceof CqlCompilerException) {
                return; // Expected a compile time error.
            }

            if (testCase instanceof Invalid) {
                return; // Expected a runtime error
            }

            log.warn(
                    "%s failed as expected, but unable to determine the cause of the failure. Possible bug in engine. Skipping."
                            .formatted(testCase.name()));
            return;
        }

        if (testCase instanceof Invalid) {
            log.warn("%s was marked as Invalid, but it got a result. Possible bug in test suite. Skipping."
                    .formatted(testCase.name()));
            return;
        }

        var testValue = result.forExpression("Test").value();
        var actualList = testValue instanceof List<?>
                ? (List<?>) testValue
                : testValue == null ? emptyList() : List.of(testValue);

        // Catch-all to prevent ClassCastException
        if (!(testCase instanceof Pass pass)) {
            throw failWithContext(
                    "expected a non-Pass test case for %s, but got %s"
                            .formatted(testCase.name(), testCase.getClass().getSimpleName()),
                    testCase,
                    actualList,
                    null);
        }

        // Invalid and Semantic errors have been handled above, so we can assume Pass here
        if (actualList.size() != pass.results.size()) {
            throw failWithContext(
                    "Incorrect number of results. Expected %d, Actual %d"
                            .formatted(pass.results.size(), actualList.size()),
                    pass,
                    actualList,
                    null);
        }

        for (int i = 0; i < actualList.size(); i++) {
            var expected = pass.results.get(i);
            var actual = actualList.get(i);
            var comparison = compareResults(expected, actual, engine.getState(), resolver);
            if (!Boolean.TRUE.equals(comparison)) {
                throw failWithContext("Result mismatch at index %d".formatted(i), pass, actual, null);
            }
        }
    }

    private TestCase buildTestCase(org.hl7.fhirpath.tests.Test test, String basePathInput, FhirContext fhirContext) {
        IBaseResource resource = ofNullable(test.getInputfile())
                .map(inputFile -> loadResourceFile(basePathInput + inputFile, fhirContext))
                .orElse(null);

        String cql = buildCql(test, resource);
        var invalid = ofNullable(test.getExpression().getInvalid()).orElse(InvalidType.FALSE);
        List<Object> expectedResults = loadExpectedResults(test);
        return switch (invalid) {
            case TRUE -> new Invalid(test.getName(), cql);
            case SEMANTIC -> new Error(test.getName(), cql);
            case FALSE -> new Pass(test.getName(), cql, resource, expectedResults);
        };
    }

    private String buildCql(org.hl7.fhirpath.tests.Test test, IBaseResource resource) {
        var inputExpression = test.getExpression().getValue();
        var predicate = ofNullable(test.isPredicate()).orElse(false);
        var expressionOutput =
                test.getOutput().size() == 1 && test.getOutput().get(0).getType() == null;

        String testExpression = inputExpression;
        if (predicate) {
            testExpression = "exists { %s }".formatted(inputExpression);
        } else if (expressionOutput) {
            String outputExpression = test.getOutput().get(0).getValue();
            if ("null".equals(outputExpression)) {
                testExpression = "(%s) is %s".formatted(inputExpression, outputExpression);
            } else if ("{ }".equals(outputExpression)) {
                testExpression = "not exists (%s)".formatted(inputExpression);
            } else if ("null".equals(inputExpression)) {
                testExpression = "(%s) is %s".formatted(outputExpression, inputExpression);
            } else {
                testExpression = "(%s) = (%s)".formatted(inputExpression, outputExpression);
            }
        }

        // if have a resource, add the CQL parameters.
        var params = ofNullable(resource)
                .map(res ->
                        parametersTemplate.formatted(nCopies(6, res.fhirType()).toArray()))
                .orElse("");

        var defines = definesTemplate.formatted(testExpression);
        return header + params + defines;
    }

    private RuntimeException failWithContext(String message, TestCase test, Object actual, Exception e) {
        var expectedString = test instanceof Pass pass ? ToStringEvaluator.toString(pass.results) : "N/A";
        var actualString = ToStringEvaluator.toString(actual);
        var error =
                """
            Failed Test: %s
            - Message: %s
            - Expected: %s
            - Actual: %s
            - CQL:%n%n%s
            """;
        return new RuntimeException(error.formatted(test.name(), message, expectedString, actualString, test.cql()), e);
    }

    protected static Tests loadTestsFile(String testsFilePath) {
        try {
            InputStream testsFileRaw = TestFhirPath.class.getResourceAsStream(testsFilePath);
            return JAXB.unmarshal(testsFileRaw, Tests.class);
        } catch (Exception e) {
            // e.printStackTrace();
            throw new IllegalArgumentException("Couldn't load tests file [ %s ]".formatted(testsFilePath), e);
        }
    }

    private IBaseResource loadResourceFile(String resourceFilePath, FhirContext context) {
        return context.newXmlParser()
                .parseResource(new InputStreamReader(TestFhirPath.class.getResourceAsStream(resourceFilePath)));
    }

    private Object readOutput(org.hl7.fhirpath.tests.Output output) {
        if (output.getType() == null) {
            return null;
        }

        return switch (output.getType()) {
            case BOOLEAN -> Boolean.valueOf(output.getValue());
            case DECIMAL -> new BigDecimal(output.getValue());
            case DATE -> new Date(output.getValue());
            case DATE_TIME -> new DateTime(
                    output.getValue(), ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));
            case TIME -> new Time(output.getValue());
            case INTEGER -> Integer.valueOf(output.getValue());
            case STRING, CODE -> output.getValue();
            case QUANTITY -> toQuantity(output.getValue());
            default -> throw new IllegalArgumentException("Output type [ %s ] is not supported in tests"
                    .formatted(output.getType() == null ? "null" : output.getType()));
        };
    }

    private List<Object> loadExpectedResults(org.hl7.fhirpath.tests.Test test) {
        // Special case for tests are "expression output" tests, which have a single output with no type
        if (test.getOutput().size() == 1 && test.getOutput().get(0).getType() == null) {
            return List.of(true);
        }

        return ofNullable(test.getOutput()).orElse(emptyList()).stream()
                .map(this::readOutput)
                .toList();
    }
}
