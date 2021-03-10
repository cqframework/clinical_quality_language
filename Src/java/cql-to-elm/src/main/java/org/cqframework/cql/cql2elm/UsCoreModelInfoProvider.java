package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class UsCoreModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isUSCoreModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equalsIgnoreCase("USCore") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir/us/core"));
        }

        return modelIdentifier.getId().equalsIgnoreCase("USCore");
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (isUSCoreModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            switch (localVersion) {
                case "3.1.0":
                default:
                    return JAXB.unmarshal(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.0.xml"),
                            ModelInfo.class);
            }
        }

        return null;
    }
}
