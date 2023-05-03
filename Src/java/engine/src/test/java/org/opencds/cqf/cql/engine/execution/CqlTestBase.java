package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;

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
}
