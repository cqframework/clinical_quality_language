package org.hl7.cql.model;

import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.IOException;

public class SystemModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isSystemModelIdentifier(ModelIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("System") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("urn:hl7-org:elm-types:r1"));
        }

        return modelIdentifier.getId().equals("System");
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (isSystemModelIdentifier(modelIdentifier)) {
            try {
                return ModelInfoReaderFactory.getReader("application/xml").read(SystemModelInfoProvider.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"));
            } catch (IOException e) {
                // Do not throw, allow other providers to resolve
            }
        }

        return null;
    }
}
