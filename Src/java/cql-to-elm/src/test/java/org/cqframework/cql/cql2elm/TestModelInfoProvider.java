package org.cqframework.cql.cql2elm;

import java.io.IOException;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

public class TestModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("Test")) {
            try {
                return ModelInfoReaderFactory.getReader("application/xml")
                        .read(TestModelInfoProvider.class.getResourceAsStream("ModelTests/test-modelinfo.xml"));
            } catch (IOException e) {
                e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the QDM model.",
                // localVersion));
            }
        }

        return null;
    }
}
