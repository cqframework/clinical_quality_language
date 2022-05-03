package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.io.IOException;

public class SystemModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isSystemModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("System") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("urn:hl7-org:elm-types:r1"));
        }

        return modelIdentifier.getId().equals("System");
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (isSystemModelIdentifier(modelIdentifier)) {
            try {
                return JacksonXML.readValue(SystemModelInfoProvider.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"),
                ModelInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the System model.", localVersion));
            }
        }

        return null;
    }
}
