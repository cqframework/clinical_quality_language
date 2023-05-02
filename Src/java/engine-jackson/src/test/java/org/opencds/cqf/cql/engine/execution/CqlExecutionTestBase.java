package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

public abstract class CqlExecutionTestBase {
    static Map<String, Library> libraries = new HashMap<>();
    Library library = null;
    private File jsonFile = null;

    private static ModelManager modelManager;
    protected static ModelManager getModelManager() {
        if (modelManager == null) {
            modelManager = new ModelManager();
        }

        return modelManager;
    }

    private static LibraryManager libraryManager;
    protected static LibraryManager getLibraryManager() {
        if (libraryManager == null) {
            libraryManager =  new LibraryManager(getModelManager());
            libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        }

        return libraryManager;
    }

    protected SignatureLevel getSignatureLevel() {
        return SignatureLevel.All;
    }

    protected CqlTranslatorOptions translatorOptions() {
        ArrayList<CqlTranslatorOptions.Options> options = new ArrayList<>();
        options.add(CqlTranslatorOptions.Options.EnableDateRangeOptimization);
        options.add(CqlTranslatorOptions.Options.EnableAnnotations);
        options.add(CqlTranslatorOptions.Options.EnableLocators);

        return new CqlTranslatorOptions(CqlCompilerException.ErrorSeverity.Info, getSignatureLevel(), options.toArray(new CqlTranslatorOptions.Options[options.size()]));
    }

    @BeforeMethod
    public void beforeEachTestMethod() throws IOException, UcumException {
        String fileName = this.getClass().getSimpleName();
        library = libraries.get(fileName);
        if (library == null) {
            UcumService ucumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));
            try {
                File cqlFile = new File(URLDecoder.decode(this.getClass().getResource(fileName + ".cql").getFile(), "UTF-8"));

                var options = this.translatorOptions();

                CqlTranslator translator = CqlTranslator.fromFile(cqlFile, getModelManager(), getLibraryManager(), ucumService,
                        CqlCompilerException.ErrorSeverity.Info, options.getSignatureLevel(), options.getOptions().toArray(new CqlTranslatorOptions.Options[options.getOptions().size()]));

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

                String json = translator.toJson();

                library = new JsonCqlLibraryReader().read(json);
                libraries.put(fileName, library);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterClass
    public void oneTimeTearDown() {
//        if (jsonFile != null) {
//            jsonFile.delete();
//        }
    }
}
