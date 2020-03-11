package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class QICoreModelInfoProvider implements ModelInfoProvider {
    private String version;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public QICoreModelInfoProvider withVersion(String version) {
        setVersion(version);
        return this;
    }

    public ModelInfo load() {
        String localVersion = version == null ? "" : version;
        switch (localVersion) {
            case "4.0.0":
            default:
                return JAXB.unmarshal(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.0.0.xml"),
                        ModelInfo.class);
        }
    }
}
