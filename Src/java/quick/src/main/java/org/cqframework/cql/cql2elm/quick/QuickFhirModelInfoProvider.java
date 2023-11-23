package org.cqframework.cql.cql2elm.quick;

import org.hl7.cql.model.NamespaceManager;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.IOException;

/**
 * Created by Bryn on 4/15/2016.
 */
public class QuickFhirModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isQuickFhirModelIdentifier(ModelIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("QUICKFHIR") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir"));
        }

        return modelIdentifier.getId().equals("QUICKFHIR");
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (isQuickFhirModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            try {
                switch (localVersion) {
                    case "3.0.1":
                    case "":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QuickFhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quickfhir-modelinfo-3.0.1.xml"));
                }
            } catch (IOException e) {
                // Do not throw, allow other providers to resolve
            }

        }

        return null;
    }
}

