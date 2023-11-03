package org.opencds.cqf.cql.engine.fhir.data;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.hl7.elm.r1.Library;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumException;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Environment;
import org.opencds.cqf.cql.engine.fhir.model.*;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

public abstract class FhirExecutionTestBase {
    public LibraryManager getLibraryManager() {
        return this.libraryManager;
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public Environment getEnvironment() {
        return new Environment(getLibraryManager());
    }

    public CqlEngine getEngine() {
        return new CqlEngine(getEnvironment());
    }

    private LibraryManager libraryManager;
    private ModelManager modelManager;

    protected Dstu2FhirModelResolver dstu2ModelResolver;
    protected RestFhirRetrieveProvider dstu2RetrieveProvider;
    protected CompositeDataProvider dstu2Provider;

    protected Dstu3FhirModelResolver dstu3ModelResolver;
    protected RestFhirRetrieveProvider dstu3RetrieveProvider;
    protected CompositeDataProvider dstu3Provider;

    protected R4FhirModelResolver r4ModelResolver;
    protected RestFhirRetrieveProvider r4RetrieveProvider;
    protected CompositeDataProvider r4Provider;

    Library library = null;
    //protected File xmlFile = null;

    @BeforeClass
    public void setup() {
        FhirContext dstu2Context = FhirContext.forCached(FhirVersionEnum.DSTU2);
        dstu2ModelResolver = new CachedDstu2FhirModelResolver();
        dstu2RetrieveProvider = new RestFhirRetrieveProvider(new SearchParameterResolver(dstu2Context),
            dstu2ModelResolver, dstu2Context.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu2"));
        dstu2Provider = new CompositeDataProvider(dstu2ModelResolver, dstu2RetrieveProvider);

        FhirContext dstu3Context = FhirContext.forCached(FhirVersionEnum.DSTU3);
        dstu3ModelResolver = new CachedDstu3FhirModelResolver();
        dstu3RetrieveProvider = new RestFhirRetrieveProvider(new SearchParameterResolver(dstu3Context), dstu3ModelResolver,
            dstu3Context.newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3"));
        dstu3Provider = new CompositeDataProvider(dstu3ModelResolver, dstu3RetrieveProvider);

        FhirContext r4Context = FhirContext.forCached(FhirVersionEnum.R4);
        r4ModelResolver = new CachedR4FhirModelResolver();
        r4RetrieveProvider = new RestFhirRetrieveProvider(new SearchParameterResolver(r4Context), r4ModelResolver,
            r4Context.newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu4"));
        r4Provider = new CompositeDataProvider(r4ModelResolver, r4RetrieveProvider);

        this.modelManager = new ModelManager();
        var compilerOptions = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.All, CqlCompilerOptions.Options.EnableDateRangeOptimization);
        this.libraryManager = new LibraryManager(modelManager, compilerOptions);
        libraryManager.getLibrarySourceLoader().clearProviders();
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
    }

    @BeforeMethod
    public void beforeEachTestMethod() throws JAXBException, IOException, UcumException {
        String fileName = this.getClass().getSimpleName();
        if (library == null) {
            try {
                File cqlFile = new File(URLDecoder.decode(this.getClass().getResource(fileName + ".cql").getFile(), "UTF-8"));

                CqlCompiler compiler = new CqlCompiler(getLibraryManager());

                var library = compiler.run(cqlFile);

                if (!compiler.getErrors().isEmpty()) {
                    System.err.println("Translation failed due to errors:");
                    ArrayList<String> errors = new ArrayList<>();
                    for (CqlCompilerException error : compiler.getErrors()) {
                        TrackBack tb = error.getLocator();
                        String lines = tb == null ? "[n/a]" : String.format("[%d:%d, %d:%d]",
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

    public static  org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name);
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }
}
