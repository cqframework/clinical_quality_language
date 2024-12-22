package org.opencds.cqf.cql.engine.fhir.data;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.cqframework.cql.cql2elm.tracking.TrackBack;
import org.fhir.ucum.UcumException;
import org.hl7.elm.r1.Library;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Environment;
import org.opencds.cqf.cql.engine.fhir.model.*;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;

public abstract class FhirExecutionTestBase {
    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    public ModelManager getModelManager() {
        return modelManager;
    }

    public Environment getEnvironment() {
        return new Environment(getLibraryManager());
    }

    public CqlEngine getEngine() {
        return new CqlEngine(getEnvironment());
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

    Library library = null;
    // protected File xmlFile = null;

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
    public void beforeEachTestMethod() throws JAXBException, IOException, UcumException {
        String fileName = this.getClass().getSimpleName();
        if (library == null) {
            try {
                File cqlFile = new File(URLDecoder.decode(
                        this.getClass().getResource(fileName + ".cql").getFile(), "UTF-8"));

                CqlCompiler compiler = new CqlCompiler(getLibraryManager());

                var library = compiler.run(cqlFile);

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

                this.library = library;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name);
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }
}
