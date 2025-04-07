package org.hl7.fhirpath;

import static org.opencds.cqf.cql.engine.elm.executing.ToQuantityEvaluator.toQuantity;

import ca.uhn.fhir.context.FhirContext;
import jakarta.xml.bind.JAXB;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import org.fhir.ucum.UcumException;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhirpath.tests.InvalidType;
import org.hl7.fhirpath.tests.Tests;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.elm.executing.ExistsEvaluator;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.fhir.model.FhirModelResolver;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Time;

public abstract class TestFhirPath {

    public static Tests loadTestsFile(String testsFilePath) {
        try {
            InputStream testsFileRaw = TestFhirPath.class.getResourceAsStream(testsFilePath);
            return JAXB.unmarshal(testsFileRaw, Tests.class);
        } catch (Exception e) {
            // e.printStackTrace();
            throw new IllegalArgumentException("Couldn't load tests file [" + testsFilePath + "]: " + e.toString());
        }
    }

    private IBaseResource loadResourceFile(String resourceFilePath, FhirContext context) {
        return context.newXmlParser()
                .parseResource(new InputStreamReader(TestFhirPath.class.getResourceAsStream(resourceFilePath)));
    }

    private Iterable<Object> loadExpectedResults(org.hl7.fhirpath.tests.Test test, boolean isExpressionOutputTest) {
        List<Object> results = new ArrayList<>();
        if (isExpressionOutputTest) {
            results.add(true);
        } else {
            if (test.getOutput() != null) {
                for (org.hl7.fhirpath.tests.Output output : test.getOutput()) {
                    if (output.getType() != null) {
                        switch (output.getType()) {
                            case BOOLEAN:
                                results.add(Boolean.valueOf(output.getValue()));
                                break;
                            case DECIMAL:
                                results.add(new BigDecimal(output.getValue()));
                                break;
                            case DATE:
                                results.add(new Date(output.getValue()));
                                break;
                            case DATE_TIME:
                                results.add(new DateTime(
                                        output.getValue(),
                                        ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
                                break;
                            case TIME:
                                results.add(new Time(output.getValue()));
                                break;
                            case INTEGER:
                                results.add(Integer.valueOf(output.getValue()));
                                break;
                            case STRING:
                                results.add(output.getValue());
                                break;
                            case CODE:
                                results.add(output.getValue());
                                break;
                            case QUANTITY:
                                results.add(toQuantity(output.getValue()));
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        String.format("Unknown output type: %s", output.getType()));
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Output type is not specified and the test is not expressed as an expression-output test");
                    }
                }
            }
        }

        return results;
    }

    abstract Boolean compareResults(
            Object expectedResult,
            Object actualResult,
            State state,
            FhirModelResolver<?, ?, ?, ?, ?, ?, ?, ?> resolver);

    @SuppressWarnings("unchecked")
    private Iterable<Object> ensureIterable(Object value) {
        Iterable<Object> actualValues;
        if (value instanceof Iterable) {
            actualValues = (Iterable<Object>) value;
        } else {
            List<Object> values = new ArrayList<Object>();
            if (value != null) {
                values.add(value);
            }
            actualValues = values;
        }
        return actualValues;
    }

    protected void runTest(
            org.hl7.fhirpath.tests.Test test,
            String basePathInput,
            FhirContext fhirContext,
            CompositeDataProvider provider,
            FhirModelResolver<?, ?, ?, ?, ?, ?, ?, ?> resolver)
            throws UcumException {
        String cql = null;
        IBaseResource resource = null;
        if (test.getInputfile() != null) {
            String resourceFilePath = basePathInput + test.getInputfile();
            resource = loadResourceFile(resourceFilePath, fhirContext);

            // TODO: Set up context based on the value of the context path in the test case
            // For now, assume %context = %resource = %rootResource and we will skip all the context and contained tests
            // Note also that tests that are hitting this type of functionality are testing how the FHIRPath engine is
            // being set up, and so are probably better tested in component or even integration tests of FHIRPath,
            // rather than as part of FHIRPath unit tests
            cql = String.format(
                    "library TestFHIRPath using FHIR version '4.0.1' include FHIRHelpers version '4.0.1' called FHIRHelpers parameter %s %s parameter \"%%context\" %s parameter \"%%resource\" %s parameter \"%%rootResource\" %s context %s define Test:",
                    resource.fhirType(), resource.fhirType(),
                    resource.fhirType(), resource.fhirType(),
                    resource.fhirType(), resource.fhirType());
        } else {
            cql =
                    "library TestFHIRPath using FHIR version '4.0.1' include FHIRHelpers version '4.0.1' called FHIRHelpers define Test:";
        }

        String testExpression = test.getExpression().getValue();
        boolean isExpressionOutputTest =
                test.getOutput().size() == 1 && test.getOutput().get(0).getType() == null;
        if (isExpressionOutputTest) {
            String outputExpression = test.getOutput().get(0).getValue();
            if ("null".equals(outputExpression)) {
                cql = String.format("%s (%s) is %s", cql, testExpression, outputExpression);
            } else if ("null".equals(testExpression)) {
                cql = String.format("%s (%s) is %s", cql, outputExpression, testExpression);
            } else {
                cql = String.format("%s (%s) = (%s)", cql, testExpression, outputExpression);
            }
        } else {
            cql = String.format("%s %s", cql, testExpression);
        }

        Library library = null;
        var env = TranslatorHelper.getEnvironment();
        env.getLibraryManager().getCompiledLibraries().clear();
        // If the test expression is invalid, expect an error during translation and
        // fail if we don't get one
        InvalidType invalidType = test.getExpression().getInvalid();
        if (invalidType == null) {
            invalidType = InvalidType.FALSE;
        }

        if (invalidType.equals(InvalidType.SEMANTIC)) {
            boolean testPassed = false;
            try {
                library = TranslatorHelper.translate(cql, env.getLibraryManager());
            } catch (Exception e) {
                testPassed = true;
            }

            if (!testPassed) {
                throw new RuntimeException(String.format("Expected exception not thrown for test %s.", test.getName()));
            }
        } else {
            try {
                library = TranslatorHelper.translate(cql, env.getLibraryManager());
            } catch (IllegalArgumentException e) {
                // if it crashes and didn't have an expected output, assume the test was supposed to fail.
                if (test.getOutput() == null || test.getOutput().isEmpty()) {
                    return;
                } else {
                    e.printStackTrace();
                    throw new RuntimeException(String.format(
                            "Couldn't translate library and was expecting a result. %s.", test.getName()));
                }
            }

            CqlEngine engine = TranslatorHelper.getEngine(env);
            engine.getCache().setExpressionCaching(false);
            engine.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", provider);
            if (resource != null) {
                engine.getState().setParameter(null, resource.fhirType(), resource);
                engine.getState().setParameter(null, "%context", resource);
                engine.getState().setParameter(null, "%resource", resource);
                engine.getState().setParameter(null, "%rootResource", resource);
            }

            Object value = null;
            boolean testPassed = false;
            String message = null;
            try {
                VersionedIdentifier libraryId = TranslatorHelper.toElmIdentifier("TestFHIRPath");
                Map<VersionedIdentifier, Library> map = new HashMap<>();
                map.put(libraryId, library);
                var results = engine.evaluate(libraryId, Set.of("Test"));

                value = results.forExpression("Test").value();
                testPassed = invalidType.equals(InvalidType.FALSE);
            } catch (Exception e) {
                testPassed = invalidType.equals(InvalidType.TRUE);
                message = e.getMessage();
            }

            if (!testPassed) {
                if (invalidType.equals(InvalidType.TRUE)) {
                    throw new RuntimeException(
                            String.format("Expected exception not thrown for test %s.", test.getName()));
                } else {
                    throw new RuntimeException(
                            String.format("Unexpected exception thrown for test %s: %s.", test.getName(), message));
                }
            }

            if (test.isPredicate() != null && test.isPredicate().booleanValue()) {
                value = ExistsEvaluator.exists(ensureIterable(value));
            }

            Iterable<Object> actualResults = ensureIterable(value);
            Iterable<Object> expectedResults = loadExpectedResults(test, isExpressionOutputTest);
            Iterator<Object> actualResultsIterator = actualResults.iterator();
            Iterator<Object> expectedResultsIterator = expectedResults.iterator();
            if (expectedResultsIterator.hasNext()) {
                for (Object expectedResult : expectedResults) {
                    if (actualResultsIterator.hasNext()) {
                        Object actualResult = actualResultsIterator.next();
                        Boolean comparison = compareResults(expectedResult, actualResult, engine.getState(), resolver);
                        if (comparison == null || !comparison) {
                            System.out.println("Failing Test: " + test.getName());
                            System.out.println("- Expected Result: " + expectedResult
                                    + (expectedResult != null ? " (" + expectedResult.getClass() + ")" : ""));
                            System.out.println("- Actual Result: " + actualResult
                                    + (actualResult != null ? " (" + actualResult.getClass() + ")" : ""));
                            throw new RuntimeException("Actual value is not equal to expected value.");
                        }
                    } else {
                        throw new RuntimeException("Actual value is not equal to expected value.");
                    }
                }
            } else {
                if (actualResultsIterator.hasNext()) {
                    throw new RuntimeException("Actual value is not equal to expected value ({})");
                }
            }
        }
    }
}
