package org.cqframework.cql.cql2elm.fhir.v18;

import org.cqframework.cql.cql2elm.*;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Bryn on 12/11/2016.
 */
public class PathTests {

    private static LibraryManager libraryManager;
    private static ModelManager modelManager;

    @BeforeClass
    public void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().clearProviders();
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        ModelInfoLoader.registerModelInfoProvider(new VersionedIdentifier().withId("FHIR").withVersion("1.8"),
                new TestFhirModelInfoProvider(PathTests.class));
    }

    @AfterClass
    public void tearDown() {
        ModelInfoLoader.unregisterModelInfoProvider(new VersionedIdentifier().withId("FHIR").withVersion("1.8"));
    }

    @Test
    public void testPaths() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(PathTests.class.getResourceAsStream("PathTests.cql"), modelManager, libraryManager);
            Library library = translator.toELM();
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
