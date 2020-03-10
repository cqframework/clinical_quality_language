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
        String localVersion = version == null ? "" : version;
        switch (localVersion) {
            case "1.0.2":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.0.2.xml"),
                        ModelInfo.class);

            case "1.4":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml"),
                        ModelInfo.class);

            case "1.6":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.6.xml"),
                        ModelInfo.class);

            case "1.8":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.8.xml"),
                        ModelInfo.class);

            case "3.0.0":
            case "":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.0.xml"),
                        ModelInfo.class);

            case "3.0.1":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.1.xml"),
                        ModelInfo.class);

            case "3.2.0":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.2.0.xml"),
                        ModelInfo.class);

            case "4.0.0":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.0.xml"),
                        ModelInfo.class);

            case "4.0.1":
                return JAXB.unmarshal(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.1.xml"),
                        ModelInfo.class);

            default:
                throw new IllegalArgumentException(String.format("Unknown version %s of the FHIR model.", localVersion));
        }
    }
}

