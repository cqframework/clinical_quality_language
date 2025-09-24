package org.cqframework.cql.cql2elm;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm.r1.Library;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ModelTests {
    private static ModelManager modelManager;
    private static ModelInfoProvider modelInfoProvider;

    @BeforeAll
    static void setup() {
        modelManager = new ModelManager();
        modelInfoProvider = new TestModelInfoProvider();
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
            translator = CqlTranslator.fromSource(
                    buffered(asSource(ModelTests.class.getResourceAsStream("ModelTests/ModelTest.cql"))), new LibraryManager(modelManager));
            Library library = translator.toELM();
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
