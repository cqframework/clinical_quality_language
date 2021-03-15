package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class QuickModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isQuickModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("QUICK") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir"));
        }

        return modelIdentifier.getId().equals("QUICK");
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (isQuickModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
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

        return null;
    }
}
