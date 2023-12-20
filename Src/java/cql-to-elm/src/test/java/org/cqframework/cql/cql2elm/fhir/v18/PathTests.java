package org.cqframework.cql.cql2elm.fhir.v18;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm.r1.Library;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Bryn on 12/11/2016.
 */
public class PathTests {

    private static LibraryManager libraryManager;
    private static ModelManager modelManager;
    private static ModelInfoProvider modelInfoProvider;

    @BeforeClass
    public void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().clearProviders();
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        modelInfoProvider = new TestFhirModelInfoProvider(PathTests.class);
        modelManager.getModelInfoLoader().registerModelInfoProvider(modelInfoProvider, true);
    }

    @AfterClass
    public void tearDown() {
        modelManager.getModelInfoLoader().unregisterModelInfoProvider(modelInfoProvider);
    }

    @Test
    public void testPaths() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(PathTests.class.getResourceAsStream("PathTests.cql"), libraryManager);
            Library library = translator.toELM();
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
