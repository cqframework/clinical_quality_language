package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

/**
 * Created by Bryn on 12/11/2016.
 */
public class TestFhirModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load() {
        return JAXB.unmarshal(TestFhirModelInfoProvider.class.getResourceAsStream("PathTests/fhir-modelinfo-1.8.xml"),
                ModelInfo.class);
    }
}
