package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

/**
 * Created by Bryn on 2/3/2016.
 */
public class QdmModelInfoProvider implements ModelInfoProvider {
    private String version;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public QdmModelInfoProvider withVersion(String version) {
        setVersion(version);
        return this;
    }

    public ModelInfo load() {
        String localVersion = version == null ? "" : version;
        switch (localVersion) {
            case "4.1.2":
                return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo.xml"),
                        ModelInfo.class);
            case "4.2":
                return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.2.xml"),
                        ModelInfo.class);
            case "4.3":
                return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.3.xml"),
                        ModelInfo.class);
            case "5.0":
                return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.xml"),
                        ModelInfo.class);
            case "5.0.1":
                return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.1.xml"),
                        ModelInfo.class);
            case "5.0.2":
                return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.2.xml"),
                        ModelInfo.class);
            case "5.3":
                return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.3.xml"),
                        ModelInfo.class);
            case "5.4":
                return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.4.xml"),
                        ModelInfo.class);
            case "5.5":
            case "":
                return JAXB.unmarshal(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.5.xml"),
                        ModelInfo.class);
            default:
                throw new IllegalArgumentException(String.format("Unknown version %s of the QDM model.", localVersion));
        }
    }
}
