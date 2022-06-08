package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.io.IOException;

public class TestModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("Test")) {
            try {
                return ModelInfoXmlReader.readValue(TestModelInfoProvider.class.getResourceAsStream("ModelTests/test-modelinfo.xml"),
                ModelInfo.class);
            } catch (IOException e) {
            e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the QDM model.", localVersion));
            }
        }

        return null;
    }
}
