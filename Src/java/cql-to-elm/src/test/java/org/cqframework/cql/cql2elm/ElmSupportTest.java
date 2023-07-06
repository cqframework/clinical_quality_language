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
        CqlCompilerOptions options = new CqlCompilerOptions( CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All);
        libraryManager = new LibraryManager(modelManager, options);

        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        try {

            var translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryJsonElm.cql"),
                    modelManager,
                    libraryManager
                   );

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithXmlElm() {
        CqlCompilerOptions options = new CqlCompilerOptions( CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All);
        libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());


        try {
            var translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryXmlElm.cql"),
                    modelManager,
                    libraryManager);

            assertTrue(translator.getErrors().size() > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithJsonWithNullTypeSpecifierElm() {
        CqlCompilerOptions options = new CqlCompilerOptions( CqlCompilerException.ErrorSeverity.Info,
                    SignatureLevel.All);
        libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        try {
            var translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryWithNullTypeSpecifierJsonElm.cql"),
                    modelManager,
                    libraryManager);

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
