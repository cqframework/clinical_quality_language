package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.hl7.elm.r1.Library;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SignatureLevelFallbackTests {

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
        try {
            CqlCompiler compiler = null;
            libraryManager = new LibraryManager(modelManager);
            libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
            compiler = new CqlCompiler(modelManager, libraryManager);
            Library library = compiler.run(LibraryTests.class.getResourceAsStream("MethodOverload.cql"),
                    CqlCompilerException.ErrorSeverity.Info,
                    LibraryBuilder.SignatureLevel.Overloads);

            assertThat(compiler.getErrors().size(), is(0));
            System.out.println(CqlTranslator.convertToJson(library));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
