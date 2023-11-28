package org.cqframework.cql.cql2elm.quick;

import org.hl7.cql.model.NamespaceManager;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.IOException;

public class UsCoreModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isUSCoreModelIdentifier(ModelIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("USCore") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir/us/core"));
        }

        return modelIdentifier.getId().equals("USCore");
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (isUSCoreModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();

            try {
                switch (localVersion) {
                    case "3.1.0":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.0.xml"));
                    case "3.1.1":
                    default:
                        return ModelInfoReaderFactory.getReader("application/xml").read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.1.xml"));
                }
            } catch (IOException e) {
                // Do not throw, allow other providers to resolve
            }

        }

        return null;
    }
}
