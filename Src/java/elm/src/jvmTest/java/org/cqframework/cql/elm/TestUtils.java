package org.cqframework.cql.elm;

import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.cql.model.NamespaceInfo;

import java.io.IOException;
import java.io.InputStream;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

public class TestUtils {
    public static CqlTranslator createTranslatorFromStream(String testFileName, CqlCompilerOptions.Options... options)
            throws IOException {
        return createTranslatorFromStream(null, testFileName, options);
    }

    public static CqlTranslator createTranslatorFromStream(
            NamespaceInfo namespaceInfo, String testFileName, CqlCompilerOptions.Options... options)
            throws IOException {
        InputStream inputStream = TestUtils.class.getResourceAsStream(testFileName);
        return createTranslatorFromStream(null, inputStream, options);
    }

    public static CqlTranslator createTranslatorFromStream(
            InputStream inputStream, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslatorFromStream(null, inputStream, options);
    }

    public static CqlTranslator createTranslatorFromStream(
            NamespaceInfo namespaceInfo, InputStream inputStream, CqlCompilerOptions.Options... options)
            throws IOException {
        ModelManager modelManager = new ModelManager();
        var compilerOptions = new CqlCompilerOptions(options);
        LibraryManager libraryManager = new LibraryManager(modelManager, compilerOptions);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        CqlTranslator translator = CqlTranslator.fromSource(namespaceInfo, buffered(asSource(inputStream)), libraryManager);
        return translator;
    }
}
