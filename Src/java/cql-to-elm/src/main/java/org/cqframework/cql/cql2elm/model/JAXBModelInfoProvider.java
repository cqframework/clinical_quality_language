package org.cqframework.cql.cql2elm.model;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class JAXBModelInfoProvider {
    public static ModelInfo load(String modelPath) {
        return JAXB.unmarshal(JAXBModelInfoProvider.class.getResourceAsStream(modelPath),
                ModelInfo.class);
    }
}
