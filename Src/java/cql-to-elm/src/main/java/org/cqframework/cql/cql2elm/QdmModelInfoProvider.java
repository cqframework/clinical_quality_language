package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class QdmModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load() {
        return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo.xml"),
                ModelInfo.class);
    }
}
