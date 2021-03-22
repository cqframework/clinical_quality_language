package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class TestModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("Test")) {
            return JAXB.unmarshal(TestModelInfoProvider.class.getResourceAsStream("ModelTests/test-modelinfo.xml"),
                    ModelInfo.class);
        }

        return null;
    }
}
