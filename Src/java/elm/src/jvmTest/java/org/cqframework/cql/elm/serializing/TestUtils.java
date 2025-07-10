package org.cqframework.cql.elm.serializing;

import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;

import java.io.IOException;

public class TestUtils {
    public static CqlTranslator createTranslator(String testFileName, CqlCompilerOptions.Options... options)
            throws IOException {
        return CqlTranslator.fromFile(testFileName, getLibraryManager(options));
    }

    private static LibraryManager getLibraryManager(CqlCompilerOptions.Options... options) {
        final ModelManager modelManager = new ModelManager();
        final CqlCompilerOptions compilerOptions = new CqlCompilerOptions(options);
        return getLibraryManager(compilerOptions, modelManager);
    }

    private static LibraryManager getLibraryManager(CqlCompilerOptions options, ModelManager modelManager) {
        final LibraryManager libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        return libraryManager;
    }
}
