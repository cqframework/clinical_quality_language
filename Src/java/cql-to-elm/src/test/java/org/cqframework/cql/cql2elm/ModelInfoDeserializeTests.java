package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ModelInfoDeserializeTests {
    private ModelManager modelManager;

    @BeforeClass
    public void setup() {
        modelManager = new ModelManager(ModelManager.ModelInfoFormat.JXSON);
    }

    @Test
    public void testModelInfo() {
        try {
            ModelInfo modelInfo = modelManager.getModelInfoLoader().getModelInfo(new VersionedIdentifier().withId("System"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
