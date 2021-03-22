package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class QICoreModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isQICoreModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("QICore") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir/us/qicore"));
        }

        return modelIdentifier.getId().equals("QICore");
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (isQICoreModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            switch (localVersion) {
                case "4.0.0":
                default:
                    return JAXB.unmarshal(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.0.0.xml"),
                            ModelInfo.class);
            }
        }

        return null;
    }
}
