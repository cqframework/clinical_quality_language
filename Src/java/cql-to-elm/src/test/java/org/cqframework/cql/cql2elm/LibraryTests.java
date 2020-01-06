package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.hl7.elm.r1.AggregateExpression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

public class LibraryTests {

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
    public void testLibraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidLibraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/InvalidReferencingLibrary.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(not(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidLibraryReference() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/InvalidLibraryReference.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(not(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDuplicateExpressionLibrary() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/DuplicateExpressionLibrary.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMissingLibrary() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/MissingLibrary.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(1));
            assertThat(translator.getErrors().get(0), instanceOf(CqlTranslatorException.class));
            assertThat(translator.getErrors().get(0).getCause(), instanceOf(CqlTranslatorIncludeException.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidBaseLibrary() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingInvalidBaseLibrary.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(1));
            assertThat(translator.getErrors().get(0), instanceOf(CqlTranslatorException.class));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTranslatorOptionsFlowDownWithAnnotations() {
        try {
            // Test Annotations are created for both libraries
            CqlTranslator translator = null;
            libraryManager = new LibraryManager(modelManager);
            libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"),
                modelManager,
                libraryManager,
                CqlTranslatorException.ErrorSeverity.Info,
                SignatureLevel.All,
                CqlTranslator.Options.EnableAnnotations);

            assertThat(translator.getErrors().size(), is(0));
            Map<String, Library> includedLibraries = translator.getLibraries();
            includedLibraries.values().stream().forEach(includedLibrary -> {
                // Ensure that some annotations are present.
                assertTrue(includedLibrary.getStatements().getDef().stream().filter(x -> x.getAnnotation().size() > 0).count() > 0);
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTranslatorOptionsFlowDownWithoutAnnotations() {

        try {
            // Test Annotations are created for both libraries
            CqlTranslator translator = null;
            libraryManager = new LibraryManager(modelManager);
            libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
            translator = CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"),
                modelManager,
                libraryManager,
                CqlTranslatorException.ErrorSeverity.Info,
                SignatureLevel.All);

            assertThat(translator.getErrors().size(), is(0));
            Map<String, Library> includedLibraries = translator.getLibraries();
            includedLibraries.values().stream().forEach(includedLibrary -> {
                // Ensure that no annotations are present.
                assertTrue(includedLibrary.getStatements().getDef().stream().filter(x -> x.getAnnotation().size() > 0).count() == 0);
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
