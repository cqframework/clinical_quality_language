package org.opencds.cqf.cql.engine.fhir.data;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cqframework.cql.cql2elm.CqlCompiler;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.hl7.elm.r1.Library;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.execution.EvaluationResultsForMultiLib;
import org.opencds.cqf.cql.engine.execution.SearchableLibraryIdentifier;
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluatedResourceTestUtils {

    private static final Logger log = LoggerFactory.getLogger(EvaluatedResourceTestUtils.class);

    static final Encounter ENCOUNTER =
            (Encounter) new Encounter().setId(new IdType(ResourceType.Encounter.name(), "Encounter1"));

    static final Condition CONDITION =
            (Condition) new Condition().setId(new IdType(ResourceType.Condition.name(), "Condition1"));

    static final Patient PATIENT = (Patient) new Patient().setId(new IdType(ResourceType.Patient.name(), "Patient1"));

    static final Procedure PROCEDURE =
            (Procedure) new Procedure().setId(new IdType(ResourceType.Procedure.name(), "Procedure1"));

    static final RetrieveProvider RETRIEVE_PROVIDER =
            (context,
                    contextPath,
                    contextValue,
                    dataType,
                    templateId,
                    codePath,
                    codes,
                    valueSet,
                    datePath,
                    dateLowPath,
                    dateHighPath,
                    dateRange) -> switch (dataType) {
                case "Encounter" -> singletonList(ENCOUNTER);
                case "Condition" -> singletonList(CONDITION);
                case "Patient" -> singletonList(PATIENT);
                case "Procedure" -> singletonList(PROCEDURE);
                default -> List.of();
            };

    static void setupCql(Class<?> classToUse, List<Library> librariesToPopulate, LibraryManager libraryManagerToUse) {
        if (librariesToPopulate.isEmpty()) {
            try {
                var resourcePaths = getResources(classToUse);

                for (var resourcePath : resourcePaths) {
                    try (var inputStream = classToUse.getClassLoader().getResourceAsStream(resourcePath)) {
                        var compiler = new CqlCompiler(libraryManagerToUse);

                        log.info("compiling CQL file: {}", resourcePath);

                        var library = compiler.run(inputStream);

                        if (!compiler.getErrors().isEmpty()) {
                            System.err.println("Translation failed due to errors:");
                            ArrayList<String> errors = new ArrayList<>();
                            for (CqlCompilerException error : compiler.getErrors()) {
                                TrackBack tb = error.getLocator();
                                String lines = tb == null
                                        ? "[n/a]"
                                        : String.format(
                                                "[%d:%d, %d:%d]",
                                                tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
                                System.err.printf("%s %s%n", lines, error.getMessage());
                                errors.add(lines + error.getMessage());
                            }
                            throw new IllegalArgumentException(errors.toString());
                        }

                        librariesToPopulate.add(library);
                    } catch (Exception exception) {
                        final String cqlFileName = resourcePath.split("/")[7];
                        final String error = "Could not retrieve CQL files on %s due to :%s"
                                .formatted(cqlFileName, exception.getMessage());
                        throw new RuntimeException(error, exception);
                    }
                }
            } catch (IOException | URISyntaxException exception) {
                final String error = "Could not retrieve CQL files due to :%s".formatted(exception.getMessage());
                throw new RuntimeException(error, exception);
            }
        }
    }

    private static List<String> getResources(Class<?> classToUse) throws IOException, URISyntaxException {
        var foundResources = new ArrayList<String>();
        var pattern = classToUse.getSimpleName() + "*.cql";

        var classLoader = classToUse.getClassLoader();
        var packagePath = classToUse.getPackage().getName().replace('.', '/');

        var urlsWithinPackage = classLoader.getResources(packagePath);

        while (urlsWithinPackage.hasMoreElements()) {
            var subPathUrl = urlsWithinPackage.nextElement();

            // Resource is on the file system.
            var dirPath = Paths.get(subPathUrl.toURI());

            findResourcesInDirectory(dirPath, packagePath, pattern, foundResources);
        }

        return foundResources;
    }

    private static void findResourcesInDirectory(
            Path directory, String packagePath, String pattern, List<String> foundResources) throws IOException {
        if (!Files.isDirectory(directory)) {
            return;
        }

        // Use a PathMatcher for the glob pattern
        var pathMatcher = directory.getFileSystem().getPathMatcher("glob:" + pattern);

        try (Stream<Path> stream = Files.list(directory)) {
            stream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> pathMatcher.matches(path.getFileName()))
                    // In the complex deps case, we want to load the "top" level libraries first, so Level5 is the
                    // furthest upstream
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        String resourceName = path.getFileName().toString();
                        // Construct the full resource path for the classloader
                        String fullResourcePath =
                                packagePath.isEmpty() ? resourceName : packagePath + "/" + resourceName;
                        foundResources.add(fullResourcePath);
                    });
        }
    }

    private static class TestRetrieveProvider implements RetrieveProvider {

        static final Encounter ENCOUNTER = (Encounter) new Encounter()
                .setPeriod(new Period().setStart(null).setEnd(null))
                .setId(new IdType(ResourceType.Encounter.name(), "Encounter1"));

        static final Condition CONDITION =
                (Condition) new Condition().setId(new IdType(ResourceType.Condition.name(), "Condition1"));

        static final Patient PATIENT =
                (Patient) new Patient().setId(new IdType(ResourceType.Patient.name(), "Patient1"));

        static final Procedure PROCEDURE =
                (Procedure) new Procedure().setId(new IdType(ResourceType.Procedure.name(), "Procedure1"));

        @Override
        public Iterable<Object> retrieve(
                String context,
                String contextPath,
                Object contextValue,
                String dataType,
                String templateId,
                String codePath,
                Iterable<Code> codes,
                String valueSet,
                String datePath,
                String dateLowPath,
                String dateHighPath,
                Interval dateRange) {
            return null;
        }
    }

    @Nonnull
    static CqlEngine getCqlEngineForFhir(
            CqlEngine cqlEngine,
            boolean expressionCaching,
            R4FhirModelResolver r4ModelResolver,
            RetrieveProvider retrieveProvider) {
        cqlEngine
                .getState()
                .getEnvironment()
                .registerDataProvider(
                        "http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, retrieveProvider));
        cqlEngine.getCache().setExpressionCaching(expressionCaching);
        return cqlEngine;
    }

    static void assertEntireEvaluationResult(
            EvaluationResultsForMultiLib evaluationResultsForMultiLib,
            SearchableLibraryIdentifier libraryIdentifier,
            Map<String, Collection<? extends IBaseResource>> expectedEvaluatedResources,
            Map<String, Collection<? extends IBaseResource>> expectedValues) {

        assertThat(evaluationResultsForMultiLib, is(notNullValue()));
        var evaluationResult = evaluationResultsForMultiLib.getResultFor(libraryIdentifier);

        if (evaluationResult == null) {
            assertThat(expectedValues.values(), empty());
            return;
        }

        var expressionResults = evaluationResult.expressionResults;

        for (String expressionName : expressionResults.keySet()) {
            var expressionResult = expressionResults.get(expressionName);

            var actualEvaluatedResourcesForName = expressionResult.evaluatedResources();
            var expectedEvaluatedResourcesForName = expectedEvaluatedResources.get(expressionName);

            assertResourcesEqual(expectedEvaluatedResourcesForName, actualEvaluatedResourcesForName);

            var actualValue = expressionResult.value();
            var expectedValue = expectedValues.get(expressionName);

            assertValuesEqual(expectedValue, actualValue);
        }
    }

    static void assertEvaluationResult(
            EvaluationResult evaluationResult,
            String expressionName,
            Collection<? extends IBaseResource> expectedEvaluatedResources) {

        var expressionResult = evaluationResult.forExpression(expressionName);
        var actualEvaluatedResources = expressionResult.evaluatedResources();
        var actualValue = expressionResult.value();

        assertResourcesEqual(expectedEvaluatedResources, actualEvaluatedResources);
        assertValuesEqual(expectedEvaluatedResources, actualValue);
    }

    static void assertEvaluationResult(
            EvaluationResultsForMultiLib evaluationResultsForMultiLib,
            SearchableLibraryIdentifier libraryIdentifier,
            String expressionName,
            Collection<? extends IBaseResource> expectedEvaluatedResources,
            Collection<? extends IBaseResource> expectedValue) {

        assertThat(evaluationResultsForMultiLib, is(notNullValue()));
        var evaluationResult = evaluationResultsForMultiLib.getResultFor(libraryIdentifier);
        var expressionResult = evaluationResult.forExpression(expressionName);
        var actualEvaluatedResources = expressionResult.evaluatedResources();
        var actualValue = expressionResult.value();

        assertResourcesEqual(expectedEvaluatedResources, actualEvaluatedResources);
        assertValuesEqual(expectedValue, actualValue);
    }

    static void assertEvaluationResult(
            EvaluationResult evaluationResult,
            String expressionName,
            Collection<? extends IBaseResource> expectedEvaluatedResources,
            Collection<? extends IBaseResource> expectedValue) {

        assertThat(evaluationResult, is(notNullValue()));
        var expressionResult = evaluationResult.forExpression(expressionName);
        var actualEvaluatedResources = expressionResult.evaluatedResources();
        var actualValue = expressionResult.value();

        assertResourcesEqual(expectedEvaluatedResources, actualEvaluatedResources);
        assertValuesEqual(expectedValue, actualValue);
    }

    private static List<IBaseResource> extractResourcesInOrder(Collection<?> resourceCandidates) {
        return resourceCandidates.stream()
                .filter(IBaseResource.class::isInstance)
                .map(IBaseResource.class::cast)
                .sorted((r1, r2) -> r1.getIdElement()
                        .getIdPart()
                        .compareTo(r2.getIdElement().getIdPart()))
                .toList();
    }

    private static void assertValuesEqual(Collection<? extends IBaseResource> expectedValue, Object actualValue) {
        assertThat(actualValue, instanceOf(List.class));
        var actualValues = (List<?>) actualValue;

        assertResourcesEqual(expectedValue, actualValues);
    }

    private static void assertResourcesEqual(Collection<?> expectedResources, Collection<?> actualResources) {
        assertThat(
                showMismatchError(expectedResources, actualResources),
                actualResources.size(),
                is(expectedResources.size()));

        var expectedResourcesList = extractResourcesInOrder(expectedResources);
        var actualResourcesList = extractResourcesInOrder(actualResources);

        for (int index = 0; index < expectedResourcesList.size(); index++) {
            var expectedResource = expectedResourcesList.get(0);
            var actualResource = actualResourcesList.get(0);

            assertResourcesEqual(expectedResource, actualResource);
        }
    }

    @Nonnull
    private static String showMismatchError(Collection<?> expectedResources, Collection<?> actualResources) {
        return "Expected: %s, actual: %s".formatted(showResources(expectedResources), showResources(actualResources));
    }

    private static String showResources(Collection<?> resources) {
        return resources.stream()
                .filter(IBaseResource.class::isInstance)
                .map(IBaseResource.class::cast)
                .map(IBaseResource::getIdElement)
                .map(IPrimitiveType::getValueAsString)
                .collect(Collectors.joining(","));
    }

    private static void assertResourcesEqual(IBaseResource expectedResource, IBaseResource actualResource) {
        assertThat(actualResource.getClass(), equalTo(expectedResource.getClass()));
        assertThat(actualResource.getIdElement(), equalTo(expectedResource.getIdElement()));
    }
}
