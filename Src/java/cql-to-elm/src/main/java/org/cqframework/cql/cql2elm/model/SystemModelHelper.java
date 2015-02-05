package org.cqframework.cql.cql2elm.model;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class SystemModelHelper {
    public static ModelInfo load() {
        return JAXB.unmarshal(SystemModelHelper.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"),
                ModelInfo.class);
    }
}
