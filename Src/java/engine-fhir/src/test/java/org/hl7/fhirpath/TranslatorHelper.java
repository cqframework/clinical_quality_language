package org.hl7.fhirpath;

import java.util.ArrayList;
import java.util.List;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Options;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.cqframework.cql.cql2elm.tracking.TrackBack;
import org.fhir.ucum.UcumException;
import org.hl7.elm.r1.Library;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Environment;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;

public class TranslatorHelper {

    private TranslatorHelper() {
        // intentionally empty
    }

    private static ModelManager modelManager = new ModelManager();
    private static LibraryManager libraryManager;

    private static LibraryManager getLibraryManager() {
        var options = CqlCompilerOptions.defaultOptions();
        options.getOptions().remove(Options.DisableListDemotion);
        options.getOptions().remove(Options.DisableListPromotion);
        options.getOptions().add(Options.EnableDateRangeOptimization);
        return getLibraryManager(options);
    }

    private static LibraryManager getLibraryManager(CqlCompilerOptions cqlCompilerOptions) {
        libraryManager = new LibraryManager(modelManager, cqlCompilerOptions);
        libraryManager.getLibrarySourceLoader().clearProviders();
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        return libraryManager;
    }

    public static CqlEngine getEngine(String cql) {
        return getEngine(getEnvironment(cql));
    }

    public static Environment getEnvironment(String cql) {
        var env = getEnvironment((TerminologyProvider) null);
        env.getLibraryManager().getCompiledLibraries().clear();
        env.getLibraryManager()
                .getLibrarySourceLoader()
                .registerProvider(new StringLibrarySourceProvider(List.of(cql)));
        return env;
    }

    public static Environment getEnvironment() {
        return getEnvironment((TerminologyProvider) null);
    }

    public static Environment getEnvironment(TerminologyProvider terminologyProvider) {
        return new Environment(getLibraryManager(), null, terminologyProvider);
    }

    public static CqlEngine getEngine(Environment environment) {
        return new CqlEngine(environment);
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name);
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }

    public static Library translate(String cql, LibraryManager libraryManager) throws UcumException {
        CqlCompiler compiler = new CqlCompiler(libraryManager);
        Library lib = compiler.run(cql);

        libraryManager.getCompiledLibraries().put(lib.getIdentifier(), compiler.getCompiledLibrary());

        if (!compiler.getErrors().isEmpty()) {
            ArrayList<String> errors = new ArrayList<>();
            for (CqlCompilerException error : compiler.getErrors()) {
                TrackBack tb = error.getLocator();
                String lines = tb == null
                        ? "[n/a]"
                        : String.format(
                                "[%d:%d, %d:%d]",
                                tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
                errors.add(lines + error.getMessage());
            }
            throw new IllegalArgumentException(errors.toString());
        }

        return lib;
    }
}
