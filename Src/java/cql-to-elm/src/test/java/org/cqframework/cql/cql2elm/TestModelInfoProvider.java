package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class TestModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load() {
        return JAXB.unmarshal(TestModelInfoProvider.class.getResourceAsStream("ModelTests/test-modelinfo.xml"),
                ModelInfo.class);
    }
}
