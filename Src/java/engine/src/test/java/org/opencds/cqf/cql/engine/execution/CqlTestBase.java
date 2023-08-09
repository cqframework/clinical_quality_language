package org.opencds.cqf.cql.engine.execution;

import java.util.List;

import org.cqframework.cql.cql2elm.*;
import org.hl7.elm.r1.Library;
import org.testng.annotations.BeforeMethod;

public class CqlTestBase {

    private static ModelManager modelManager;
    protected static ModelManager getModelManager() {
        if (modelManager == null) {
            modelManager = new ModelManager();
        }

        return modelManager;
    }

    protected static LibraryManager getLibraryManager() {
        return getLibraryManager(createOptionsMin());
    }

    protected static LibraryManager getLibraryManager(CqlCompilerOptions compilerOptions) {
        var manager = new LibraryManager(getModelManager(), compilerOptions);
        manager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

        return manager;
    }

    public Library getLibrary(org.hl7.elm.r1.VersionedIdentifier libraryId, List<CqlCompilerException> errors, CqlCompilerOptions options) {
        return getLibraryManager(options).resolveLibrary(libraryId, errors).getLibrary();
    }


    public Library getLibrary(org.hl7.elm.r1.VersionedIdentifier libraryId, List<CqlCompilerException> errors) {
        return environment.getLibraryManager().resolveLibrary(libraryId, errors).getLibrary();
    }

    public Library getLibrary(org.hl7.elm.r1.VersionedIdentifier libraryId) {
        return this.getLibrary(libraryId, null);
    }

    public Library toLibrary(String text) {
        return toLibrary(text, getLibraryManager());
    }

    public Library toLibrary(String text, LibraryManager libraryManager) {
        CqlTranslator translator = CqlTranslator.fromText(text, libraryManager);
        return translator.toELM();
    }

    public static  org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name);
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }

    Environment environment;
    CqlEngine engine;
    @BeforeMethod
    protected void beforeEachMethod(){
        environment = new Environment(getLibraryManager());
        engine = new CqlEngine(environment);
    }

    public static CqlCompilerOptions createOptionsMin() {
        CqlCompilerOptions result = new CqlCompilerOptions();
        result.setOptions(CqlCompilerOptions.Options.EnableDateRangeOptimization,
                CqlCompilerOptions.Options.EnableLocators,
                CqlCompilerOptions.Options.EnableResultTypes,
                CqlCompilerOptions.Options.DisableListDemotion,
                CqlCompilerOptions.Options.DisableListPromotion,
                CqlCompilerOptions.Options.DisableMethodInvocation);

        return result;
    }
}
