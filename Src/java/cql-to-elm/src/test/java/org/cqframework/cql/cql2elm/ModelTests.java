package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm.r1.Library;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ModelTests {
    private ModelManager modelManager;
    private ModelInfoProvider modelInfoProvider;

    @BeforeClass
    public void setup() {
        modelManager = new ModelManager();
        modelInfoProvider = new TestModelInfoProvider();
        modelManager.getModelInfoLoader().registerModelInfoProvider(modelInfoProvider);
    }

    @AfterClass
    public void tearDown() {
        modelManager.getModelInfoLoader().unregisterModelInfoProvider(modelInfoProvider);
    }

    @Test
    public void testModelInfo() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    ModelTests.class.getResourceAsStream("ModelTests/ModelTest.cql"), new LibraryManager(modelManager));
            Library library = translator.toELM();
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
