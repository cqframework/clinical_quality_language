package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

/**
 * Created by Bryn on 4/15/2016.
 */
public class FhirModelInfoProvider implements ModelInfoProvider {
    private String version;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public FhirModelInfoProvider withVersion(String version) {
        setVersion(version);
        return this;
    }

    public ModelInfo load() {
        switch (version) {
            case "1.4":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml"),
                        ModelInfo.class);

            case "1.6":
            default:
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.6.xml"),
                        ModelInfo.class);
        }
    }
}

