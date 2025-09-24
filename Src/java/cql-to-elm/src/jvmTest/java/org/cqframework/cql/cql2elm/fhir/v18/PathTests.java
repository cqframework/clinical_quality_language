package org.cqframework.cql.cql2elm.fhir.v18;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm.r1.Library;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Created by Bryn on 12/11/2016.
 */
class PathTests {

    private static LibraryManager libraryManager;
    private static ModelManager modelManager;
    private static ModelInfoProvider modelInfoProvider;

    @BeforeAll
    static void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().clearProviders();
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        modelInfoProvider = new TestFhirModelInfoProvider(PathTests.class);
        modelManager.getModelInfoLoader().registerModelInfoProvider(modelInfoProvider, true);
    }

    @AfterAll
    static void tearDown() {
        modelManager.getModelInfoLoader().unregisterModelInfoProvider(modelInfoProvider);
    }

    @Test
    void paths() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromSource(buffered(asSource(PathTests.class.getResourceAsStream("PathTests.cql"))), libraryManager);
            Library library = translator.toELM();
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
