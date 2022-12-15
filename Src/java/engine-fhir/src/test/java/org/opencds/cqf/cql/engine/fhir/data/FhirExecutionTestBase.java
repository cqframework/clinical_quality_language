package org.opencds.cqf.cql.engine.fhir.data;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.fhir.model.*;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public abstract class FhirExecutionTestBase {
    static Map<String, Library> libraries = new HashMap<>();


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

    }

    @BeforeMethod
    public void beforeEachTestMethod() throws JAXBException, IOException, UcumException {
        String fileName = this.getClass().getSimpleName();
        library = libraries.get(fileName);
        if (library == null) {
            ModelManager modelManager = new ModelManager();
            LibraryManager libraryManager = new LibraryManager(modelManager);
            libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
            UcumService ucumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));
            try {
                File cqlFile = new File(URLDecoder.decode(this.getClass().getResource("fhir/" + fileName + ".cql").getFile(), "UTF-8"));

                ArrayList<CqlTranslatorOptions.Options> options = new ArrayList<>();
                options.add(CqlTranslatorOptions.Options.EnableDateRangeOptimization);

                CqlTranslator translator = CqlTranslator.fromFile(cqlFile, modelManager, libraryManager, ucumService, options.toArray(new CqlTranslatorOptions.Options[options.size()]));

                if (translator.getErrors().size() > 0) {
                    System.err.println("Translation failed due to errors:");
                    ArrayList<String> errors = new ArrayList<>();
                    for (CqlCompilerException error : translator.getErrors()) {
                        TrackBack tb = error.getLocator();
                        String lines = tb == null ? "[n/a]" : String.format("[%d:%d, %d:%d]",
                            tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
                        System.err.printf("%s %s%n", lines, error.getMessage());
                        errors.add(lines + error.getMessage());
                    }
                    throw new IllegalArgumentException(errors.toString());
                }

                assertThat(translator.getErrors().size(), is(0));

                for (Map.Entry<String, CompiledLibrary> entry : libraryManager.getCompiledLibraries().entrySet()) {
                    String jsonContent = CqlTranslator.convertToJson(entry.getValue().getLibrary());
                    StringReader sr = new StringReader(jsonContent);
                    libraries.put(entry.getKey(), new JsonCqlLibraryReader().read(sr));
                    if (entry.getKey().equals(fileName)) {
                        library = libraries.get(entry.getKey());
                    }
                }

                if (library == null) {
                    library = new JsonCqlLibraryReader().read(new StringReader(translator.toJson()));
                    libraries.put(fileName, library);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
