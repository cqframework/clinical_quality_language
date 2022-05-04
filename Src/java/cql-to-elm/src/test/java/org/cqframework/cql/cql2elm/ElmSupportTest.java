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
    public void testIncludedLibraryWithJxsonElm() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmJxsonLibrarySourceProvider());

        CqlCompilerOptions options = createOptions();
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryJxsonElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    options.getOptions().toArray(new CqlCompilerOptions.Options[0]));

            assertTrue(translator.getErrors().size() > 0);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithJsonElm() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmJsonLibrarySourceProvider());
        try {
            CqlCompilerOptions options = createOptions();
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryJsonElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    options.getOptions().toArray(new CqlCompilerOptions.Options[0]));

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithXmlElm() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmXmlLibrarySourceProvider());

        CqlCompilerOptions options = createOptions();
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryXmlElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All, options.getOptions().toArray(new CqlCompilerOptions.Options[0]));

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithJsonWithNullTypeSpecifierElm() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmJsonLibrarySourceProvider());
        try {
            CqlCompilerOptions options = createOptions();
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryWithNullTypeSpecifierJsonElm.cql"),
                    modelManager,
                    libraryManager,
                    CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    options.getOptions().toArray(new CqlCompilerOptions.Options[0]));

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CqlCompilerOptions createOptions() {
        CqlCompilerOptions result = new CqlCompilerOptions();
        result.setOptions(CqlCompilerOptions.Options.EnableDateRangeOptimization,
                CqlCompilerOptions.Options.EnableAnnotations,
                CqlCompilerOptions.Options.EnableLocators,
                CqlCompilerOptions.Options.EnableResultTypes,
                CqlCompilerOptions.Options.DisableListDemotion,
                CqlCompilerOptions.Options.DisableListPromotion,
                CqlCompilerOptions.Options.DisableMethodInvocation);

        return result;
    }

}
