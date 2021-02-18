package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
            translator = CqlTranslator.fromStream(ModelTests.class.getResourceAsStream("ModelTests/ModelTest.cql"), modelManager, new LibraryManager(modelManager));
            Library library = translator.toELM();
            assertThat(translator.getErrors().size(), is(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
