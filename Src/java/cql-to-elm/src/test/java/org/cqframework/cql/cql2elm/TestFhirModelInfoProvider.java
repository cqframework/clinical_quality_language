package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

/**
 * Created by Bryn on 12/11/2016.
 */
public class TestFhirModelInfoProvider implements ModelInfoProvider {
    private Class clazz;

    public TestFhirModelInfoProvider(Class clazz) {
        this.clazz = clazz;
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("FHIR")) {
            return JAXB.unmarshal(clazz.getResourceAsStream("fhir-modelinfo-1.8.xml"),
                    ModelInfo.class);
        }

        return null;
    }
}
