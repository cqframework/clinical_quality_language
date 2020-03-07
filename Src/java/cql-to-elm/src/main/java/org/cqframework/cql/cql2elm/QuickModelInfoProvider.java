package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class QuickModelInfoProvider implements ModelInfoProvider {
    private String version;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public QuickModelInfoProvider withVersion(String version) {
        setVersion(version);
        return this;
    }

    public ModelInfo load() {
        String localVersion = version == null ? "" : version;
        switch (localVersion) {
            case "3.3.0":
                return JAXB.unmarshal(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.3.0.xml"),
                        ModelInfo.class);
            case "3.0.0":
                return JAXB.unmarshal(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.0.0.xml"),
                        ModelInfo.class);
            default:
                return JAXB.unmarshal(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo.xml"),
                        ModelInfo.class);
        }
    }
}
