package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;

public class SystemModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isSystemModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("System") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://cql.hl7.org/public"));
        }

        return modelIdentifier.getId().equals("System");
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (isSystemModelIdentifier(modelIdentifier)) {
            return JAXB.unmarshal(SystemModelInfoProvider.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"),
                    ModelInfo.class);
        }

        return null;
    }
}
