package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.testng.annotations.BeforeMethod;

public class CqlTestBase {

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

    public static  org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name);
    }

    public static org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }

    Environment environment;
    CqlEngineVisitor engineVisitor;
    @BeforeMethod
    protected void beforeEachMethod(){
        environment = new Environment(getLibraryManager());
        engineVisitor = new CqlEngineVisitor(environment, null, null, null, createOptionsMin());
    }

    public static CqlTranslatorOptions createOptionsMin() {
        CqlTranslatorOptions result = new CqlTranslatorOptions();
        result.setOptions(CqlTranslatorOptions.Options.EnableDateRangeOptimization,
                CqlTranslatorOptions.Options.EnableLocators,
                CqlTranslatorOptions.Options.EnableResultTypes,
                CqlTranslatorOptions.Options.DisableListDemotion,
                CqlTranslatorOptions.Options.DisableListPromotion,
                CqlTranslatorOptions.Options.DisableMethodInvocation);

        return result;
    }
}
