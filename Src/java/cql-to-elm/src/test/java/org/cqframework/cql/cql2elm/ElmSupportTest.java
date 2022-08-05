package org.cqframework.cql.cql2elm;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ElmSupportTest {

    ModelManager modelManager;
    LibraryManager libraryManager;

    @BeforeClass
    public void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
    }

    @AfterClass
    public void tearDown() {
        libraryManager.getLibrarySourceLoader().clearProviders();
    }

    @Test
    public void testIncludedLibraryWithJsonElm() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        try {
            CqlTranslatorOptions options = createOptions();
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryJsonElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    options.getOptions().toArray(new CqlTranslatorOptions.Options[0]));

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithXmlElm() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

        CqlTranslatorOptions options = createOptions();
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryXmlElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All, options.getOptions().toArray(new CqlTranslatorOptions.Options[0]));

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithJsonWithNullTypeSpecifierElm() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        try {
            CqlTranslatorOptions options = createOptions();
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryWithNullTypeSpecifierJsonElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    options.getOptions().toArray(new CqlTranslatorOptions.Options[0]));

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CqlTranslatorOptions createOptions() {
        CqlTranslatorOptions result = new CqlTranslatorOptions();
        result.setOptions(CqlTranslatorOptions.Options.EnableDateRangeOptimization,
                CqlTranslatorOptions.Options.EnableAnnotations,
                CqlTranslatorOptions.Options.EnableLocators,
                CqlTranslatorOptions.Options.EnableResultTypes,
                CqlTranslatorOptions.Options.DisableListDemotion,
                CqlTranslatorOptions.Options.DisableListPromotion,
                CqlTranslatorOptions.Options.DisableMethodInvocation);

        return result;
    }

}
