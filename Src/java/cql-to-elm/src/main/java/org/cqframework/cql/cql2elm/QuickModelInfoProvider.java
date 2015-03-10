package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class QuickModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load() {
        return JAXB.unmarshal(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo.xml"),
                ModelInfo.class);
    }
}
