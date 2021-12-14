package org.cqframework.cql.cql2elm;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
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
    public void testIncludedLibraryWithJxsonEml() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmJxsonLibrarySourceProvider());

        CqlTranslatorOptions options = createOptions();
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryJxsonElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlTranslatorException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    options.getOptions().toArray(new CqlTranslator.Options[0]));

            assertTrue(translator.getErrors().size() > 0);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithJsonEml() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmJsonLibrarySourceProvider());
        try {
            CqlTranslatorOptions options = createOptions();
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryJsonElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlTranslatorException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    options.getOptions().toArray(new CqlTranslator.Options[0]));

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithXmlEml() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmXmlLibrarySourceProvider());

        CqlTranslatorOptions options = createOptions();
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryXmlElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlTranslatorException.ErrorSeverity.Info,
                    SignatureLevel.All, options.getOptions().toArray(new CqlTranslator.Options[0]));

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithJsonWithNullTypeSpecifierEml() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmJsonLibrarySourceProvider());
        try {
            CqlTranslatorOptions options = createOptions();
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryWithNullTypeSpecifierJsonElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlTranslatorException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    options.getOptions().toArray(new CqlTranslator.Options[0]));

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CqlTranslatorOptions createOptions() {
        CqlTranslatorOptions result = new CqlTranslatorOptions();
        result.setOptions(CqlTranslator.Options.EnableDateRangeOptimization,
                CqlTranslator.Options.EnableAnnotations,
                CqlTranslator.Options.EnableLocators,
                CqlTranslator.Options.EnableResultTypes,
                CqlTranslator.Options.DisableListDemotion,
                CqlTranslator.Options.DisableListPromotion,
                CqlTranslator.Options.DisableMethodInvocation);

        return result;
    }

}
