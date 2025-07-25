package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.jupiter.api.Assertions.fail;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.cqframework.cql.cql2elm.CqlCompiler;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Environment;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.execution.ExpressionResult;
import org.opencds.cqf.cql.engine.fhir.model.CachedDstu2FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.CachedDstu3FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.CachedR4FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.Dstu2FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FhirExecutionMultiLibTestBase {
    private static final Logger log = LoggerFactory.getLogger(FhirExecutionMultiLibTestBase.class);

    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    public ModelManager getModelManager() {
        return modelManager;
    }

    public Environment getEnvironmentWithExistingLibraryManager() {
        return new Environment(getLibraryManager());
    }

    public Environment getEnvironmentWithNewLibraryManager() {
        return new Environment(buildNewLibraryManager());
    }

    public CqlEngine getEngineWithExistingLibraryManager() {
        return new CqlEngine(getEnvironmentWithExistingLibraryManager());
    }

    public CqlEngine getEngineWithNewLibraryManager() {
        return new CqlEngine(getEnvironmentWithNewLibraryManager());
    }

    private static LibraryManager libraryManager;
    private static ModelManager modelManager;

    protected static Dstu2FhirModelResolver dstu2ModelResolver;
    protected static RestFhirRetrieveProvider dstu2RetrieveProvider;
    protected static CompositeDataProvider dstu2Provider;

    protected static Dstu3FhirModelResolver dstu3ModelResolver;
    protected static RestFhirRetrieveProvider dstu3RetrieveProvider;
    protected static CompositeDataProvider dstu3Provider;

    protected static R4FhirModelResolver r4ModelResolver;
    protected static RestFhirRetrieveProvider r4RetrieveProvider;
    protected static CompositeDataProvider r4Provider;

    private final List<Library> libraries = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        FhirContext dstu2Context = FhirContext.forCached(FhirVersionEnum.DSTU2);
        dstu2ModelResolver = new CachedDstu2FhirModelResolver();
        dstu2RetrieveProvider = new RestFhirRetrieveProvider(
                new SearchParameterResolver(dstu2Context),
                dstu2ModelResolver,
                dstu2Context.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu2"));
        dstu2Provider = new CompositeDataProvider(dstu2ModelResolver, dstu2RetrieveProvider);

        FhirContext dstu3Context = FhirContext.forCached(FhirVersionEnum.DSTU3);
        dstu3ModelResolver = new CachedDstu3FhirModelResolver();
        dstu3RetrieveProvider = new RestFhirRetrieveProvider(
                new SearchParameterResolver(dstu3Context),
                dstu3ModelResolver,
                dstu3Context.newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3"));
        dstu3Provider = new CompositeDataProvider(dstu3ModelResolver, dstu3RetrieveProvider);

        FhirContext r4Context = FhirContext.forCached(FhirVersionEnum.R4);
        r4ModelResolver = new CachedR4FhirModelResolver();
        r4RetrieveProvider = new RestFhirRetrieveProvider(
                new SearchParameterResolver(r4Context),
                r4ModelResolver,
                r4Context.newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu4"));
        r4Provider = new CompositeDataProvider(r4ModelResolver, r4RetrieveProvider);

        modelManager = new ModelManager();
        var compilerOptions = CqlCompilerOptions.defaultOptions();
        libraryManager = new LibraryManager(modelManager, compilerOptions);
        libraryManager.getLibrarySourceLoader().clearProviders();
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
    }

    @BeforeEach
    public void beforeEachTestMethod() {
        String filePrefix = this.getClass().getSimpleName();
        if (libraries.isEmpty()) {
            try {
                var resourcePaths = getResources(filePrefix);

                for (var resourcePath : resourcePaths) {
                    try (var inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                        var compiler = new CqlCompiler(getLibraryManager());

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

                        libraries.add(library);
                    }
                }

            } catch (Exception exception) {
                fail("Could not retrieve CQL files due to :" + exception.getMessage());
            }
        }
    }

    private List<String> getResources(String fileNamePrefix) throws IOException, URISyntaxException {
        var foundResources = new ArrayList<String>();
        var thisClass = this.getClass();
        var pattern = fileNamePrefix + "*.cql";

        var classLoader = thisClass.getClassLoader();
        var packagePath = thisClass.getPackage().getName().replace('.', '/');

        var urlsWithinPackage = classLoader.getResources(packagePath);

        while (urlsWithinPackage.hasMoreElements()) {
            var subPathUrl = urlsWithinPackage.nextElement();

            // Resource is on the file system.
            var dirPath = Paths.get(subPathUrl.toURI());

            findResourcesInDirectory(dirPath, packagePath, pattern, foundResources);
        }

        return foundResources;
    }

    private void findResourcesInDirectory(
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

    protected List<VersionedIdentifier> getAllLibraryIdentifiers() {
        return libraries.stream().map(Library::getIdentifier).toList();
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name);
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }

    private LibraryManager buildNewLibraryManager() {
        var libraryManagerInner = new LibraryManager(new ModelManager(), CqlCompilerOptions.defaultOptions());

        libraryManagerInner.getLibrarySourceLoader().clearProviders();
        libraryManagerInner.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        libraryManagerInner.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

        return libraryManagerInner;
    }

    protected static String printEvaluationResult(CqlEngine.EvaluationResultsForMultiLib evaluationResultsForMultiLib) {
        if (evaluationResultsForMultiLib == null) {
            return "null";
        }
        return "\nEvaluationResultsForMultiLib{" + "evaluationResults=\n"
                + evaluationResultsForMultiLib.getResults().entrySet().stream()
                        .map(entry -> new DefaultMapEntry<>(entry.getKey(), printEvaluationResult(entry.getValue())))
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining("\n"))
                + "}\n";
    }

    protected static String printEvaluationResult(EvaluationResult evaluationResult) {
        if (evaluationResult == null) {
            return "null";
        }
        return "\nEvaluationResult{" + "expressionResults=\n"
                + evaluationResult.expressionResults.entrySet().stream()
                        .map(entry -> new DefaultMapEntry<>(entry.getKey(), printExpressionResult(entry.getValue())))
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining("\n"))
                + "}\n";
    }

    protected static String printExpressionResult(ExpressionResult expressionResult) {
        if (expressionResult == null) {
            return "\nnull";
        }

        return "\nExpressionResult{value=" + showValue(expressionResult.value()) + ", type="
                + showEvaluatedResources(expressionResult.evaluatedResources()) + '}';
    }

    protected static String showValue(Object valueOrCollection) {
        if (valueOrCollection == null) {
            return "null";
        }
        if (valueOrCollection instanceof Collection<?> collection) {
            return showEvaluatedResources(collection);
        }
        return showEvaluatedResource(valueOrCollection);
    }

    protected static String showEvaluatedResources(Collection<?> evaluatedResourcesOrSomethings) {
        return evaluatedResourcesOrSomethings.stream()
                .map(FhirExecutionMultiLibTestBase::showEvaluatedResource)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    protected static String showEvaluatedResource(Object evaluatedResourceOrSomething) {
        if (evaluatedResourceOrSomething instanceof IBaseResource resource) {
            return resource.getIdElement().getValueAsString();
        } else if (evaluatedResourceOrSomething != null) {
            return evaluatedResourceOrSomething.toString();
        } else {
            return "null";
        }
    }
}
