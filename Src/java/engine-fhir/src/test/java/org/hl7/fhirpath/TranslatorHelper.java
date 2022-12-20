package org.hl7.fhirpath;

import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader;
import org.opencds.cqf.cql.engine.execution.LibraryLoader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class TranslatorHelper {
    private static ModelManager modelManager = new ModelManager();
    private static LibraryManager libraryManager;

    private static LibraryManager getLibraryManager() {
        if (libraryManager == null) {
            libraryManager = new LibraryManager(modelManager);
            libraryManager.getLibrarySourceLoader().clearProviders();
            libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
            libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        }
        return libraryManager;
    }

    private LibraryLoader libraryLoader;

    public LibraryLoader getLibraryLoader() {
        if (libraryLoader == null) {
            libraryLoader = new TestLibraryLoader(getLibraryManager());
        }
        return libraryLoader;
    }

    public Library translate(String cql) throws UcumException {
        ArrayList<CqlTranslatorOptions.Options> options = new ArrayList<>();
        options.add(CqlTranslatorOptions.Options.EnableDateRangeOptimization);
        UcumService ucumService = new UcumEssenceService(
            UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));

        CqlTranslator translator = CqlTranslator.fromText(cql, modelManager, getLibraryManager(), ucumService,
            options.toArray(new CqlTranslatorOptions.Options[options.size()]));
        if (translator.getErrors().size() > 0) {
            ArrayList<String> errors = new ArrayList<>();
            for (CqlCompilerException error : translator.getErrors()) {
                TrackBack tb = error.getLocator();
                String lines = tb == null ? "[n/a]"
                    : String.format("[%d:%d, %d:%d]", tb.getStartLine(), tb.getStartChar(), tb.getEndLine(),
                    tb.getEndChar());
                errors.add(lines + error.getMessage());
            }
            throw new IllegalArgumentException(errors.toString());
        }

        String json = translator.toJson();

        try {
            return new JsonCqlLibraryReader().read(new StringReader(json));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
