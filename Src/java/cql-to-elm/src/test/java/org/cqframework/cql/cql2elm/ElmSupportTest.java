package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.hl7.elm.r1.*;
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
    public void testIncludedLibraryWithJsonEml() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmJsonLibrarySourceProvider());

        CqlTranslatorOptions options = createOptions();
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryEml.cql"),
                    modelManager,
                    libraryManager,
                    CqlTranslatorException.ErrorSeverity.Info,
                    SignatureLevel.All,
                    options.getOptions().toArray(new CqlTranslator.Options[0]));

            assertThat(translator.getErrors().size(), is(0));

            Map<String, ExpressionDef> includedLibDefs = new HashMap<>();
            Map<String, Library> includedLibraries = translator.getLibraries();
            includedLibraries.values().stream().forEach(includedLibrary -> {
                if (includedLibrary.getStatements() != null) {
                    for (ExpressionDef def : includedLibrary.getStatements().getDef()) {
                        includedLibDefs.put(def.getName(), def);
                    }
                }
            });

            ExpressionDef sdeLibDef = includedLibDefs.get("SDE Ethnicity");
            assertNotNull(sdeLibDef);
            assertThat(includedLibraries.size(), is(1));
            assertThat(includedLibDefs.size(), is(5));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncludedLibraryWithXmlEml() {
        CqlTranslator translator = null;
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new ElmXmlLibrarySourceProvider());

        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibraryEml.cql"),
                    modelManager,
                    libraryManager,
                    CqlTranslatorException.ErrorSeverity.Info,
                    SignatureLevel.All);

            assertThat(translator.getErrors().size(), is(0));

            Map<String, ExpressionDef> includedLibDefs = new HashMap<>();
            Map<String, Library> includedLibraries = translator.getLibraries();
            includedLibraries.values().stream().forEach(includedLibrary -> {
                if (includedLibrary.getStatements() != null) {
                    for (ExpressionDef def : includedLibrary.getStatements().getDef()) {
                        includedLibDefs.put(def.getName(), def);
                    }
                }
            });

            ExpressionDef sdeLibDef = includedLibDefs.get("SDE Ethnicity");
            assertNotNull(sdeLibDef);
            assertThat(includedLibraries.size(), is(1));
            assertThat(includedLibDefs.size(), is(5));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CqlTranslatorOptions createOptions(){
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
