package org.cqframework.cql.poc.translator.model;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;
import java.io.File;

public class QuickModelHelper {
    public static ModelInfo load() {
        return JAXB.unmarshal(QuickModelHelper.class.getResourceAsStream("resources/org/hl7/fhir/quick-modelinfo.xml"),
                ModelInfo.class);
    }
}
