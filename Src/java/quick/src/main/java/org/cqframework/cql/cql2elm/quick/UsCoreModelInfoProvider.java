package org.cqframework.cql.cql2elm.quick;

import org.cqframework.cql.cql2elm.ModelInfoProvider;
import org.cqframework.cql.cql2elm.NamespaceManager;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.IOException;

public class UsCoreModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isUSCoreModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("USCore") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir/us/core"));
        }

        return modelIdentifier.getId().equals("USCore");
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
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
                e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the Fhir model.", localVersion));
            }
            
        }

        return null;
    }
}
