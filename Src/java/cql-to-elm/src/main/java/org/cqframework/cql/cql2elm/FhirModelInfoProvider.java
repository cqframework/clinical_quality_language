package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

/**
 * Created by Bryn on 4/15/2016.
 */
public class FhirModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load() {
        return JAXB.unmarshal(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml"),
                ModelInfo.class);
    }
}

