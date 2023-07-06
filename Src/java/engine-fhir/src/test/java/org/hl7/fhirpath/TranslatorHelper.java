package org.hl7.fhirpath;

import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.hl7.elm.r1.Library;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumException;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Environment;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;

import java.util.ArrayList;

public class TranslatorHelper {
    private static ModelManager modelManager = new ModelManager();
    private static LibraryManager libraryManager;

    private static LibraryManager getLibraryManager() {
      return getLibraryManager(CqlCompilerOptions.defaultOptions());
    }

    private static LibraryManager getLibraryManager(CqlCompilerOptions cqlCompilerOptions) {
        libraryManager = new LibraryManager(modelManager, cqlCompilerOptions);
        libraryManager.getLibrarySourceLoader().clearProviders();
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        return libraryManager;
    }

    public static Environment getEnvironment() {
        return getEnvironment(null);
    }


    public static Environment getEnvironment(TerminologyProvider terminologyProvider) {
        return new Environment(getLibraryManager(), null, terminologyProvider);
    }

    public static CqlEngine getEngineVisitor() {
        return getEngineVisitor(null);
    }

    public static CqlEngine getEngineVisitor(TerminologyProvider terminologyProvider) {
        return new CqlEngine(getEnvironment(terminologyProvider));
    }

    public static  org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name);
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }

    public Library translate(String cql) throws UcumException {
        var compilerOptions = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.All, CqlCompilerOptions.Options.EnableDateRangeOptimization);
        CqlTranslator translator = CqlTranslator.fromText(cql, modelManager, getLibraryManager(compilerOptions));
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

        return translator.toELM();
    }
}
