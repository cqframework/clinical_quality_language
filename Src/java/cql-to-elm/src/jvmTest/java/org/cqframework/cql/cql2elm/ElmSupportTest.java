package org.cqframework.cql.cql2elm;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ElmSupportTest {

    static ModelManager modelManager;
    static LibraryManager libraryManager;

    @BeforeAll
    static void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
    }

    @AfterAll
    static void tearDown() {
        libraryManager.getLibrarySourceLoader().clearProviders();
    }

    @Test
    void includedLibraryWithJsonElm() {
        CqlCompilerOptions options =
                new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, SignatureLevel.All);
        libraryManager = new LibraryManager(modelManager, options);

        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        try {

            var translator = CqlTranslator.fromSource(
                    buffered(asSource(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryJsonElm.cql"))),
                    libraryManager);

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void includedLibraryWithXmlElm() {
        CqlCompilerOptions options =
                new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, SignatureLevel.All);
        libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

        try {
            var translator = CqlTranslator.fromSource(
                    buffered(asSource(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryXmlElm.cql"))),
                    libraryManager);

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void includedLibraryWithJsonWithNullTypeSpecifierElm() {
        CqlCompilerOptions options =
                new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, SignatureLevel.All);
        libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        try {
            var translator = CqlTranslator.fromSource(
                    buffered(asSource(LibraryTests.class.getResourceAsStream(
                            "LibraryTests/ReferencingLibraryWithNullTypeSpecifierJsonElm.cql"))),
                    libraryManager);

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CqlCompilerOptions createOptions() {
        CqlCompilerOptions result = new CqlCompilerOptions();
        result.setOptions(
                CqlCompilerOptions.Options.EnableDateRangeOptimization,
                CqlCompilerOptions.Options.EnableAnnotations,
                CqlCompilerOptions.Options.EnableLocators,
                CqlCompilerOptions.Options.EnableResultTypes,
                CqlCompilerOptions.Options.DisableListDemotion,
                CqlCompilerOptions.Options.DisableListPromotion,
                CqlCompilerOptions.Options.DisableMethodInvocation);

        return result;
    }
}
