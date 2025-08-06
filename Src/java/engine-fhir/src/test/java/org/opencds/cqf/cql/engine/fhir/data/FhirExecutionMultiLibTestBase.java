package org.opencds.cqf.cql.engine.fhir.data;

import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.setupCql;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import java.util.ArrayList;
import java.util.List;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Environment;
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

    // TODO: LD: figure out how to compile the CQLs only once for the whole test class
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
        setupCql(this.getClass(), libraries, libraryManager);
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
}
