package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm.r1.Library;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ModelWithoutDefaultLoadersTests {
    private static ModelManager modelManager;
    private static ModelInfoProvider modelInfoProvider;

    @BeforeAll
    static void setup() {
        modelManager = new ModelManager(false);
        modelInfoProvider = new TestModelInfoProvider();
        // modelManager.getModelInfoLoader().registerSystemModelInfoProvider();
        modelManager.getModelInfoLoader().registerModelInfoProvider(modelInfoProvider);
    }

    @AfterAll
    static void tearDown() {
        modelManager.getModelInfoLoader().unregisterModelInfoProvider(modelInfoProvider);
    }

    @Test
    void modelInfo() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    ModelWithoutDefaultLoadersTests.class.getResourceAsStream("ModelTests/ModelTest.cql"),
                    new LibraryManager(modelManager));
            Library library = translator.toELM();
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
