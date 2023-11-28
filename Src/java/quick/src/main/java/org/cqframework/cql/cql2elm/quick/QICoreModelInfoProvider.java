package org.cqframework.cql.cql2elm.quick;

import org.hl7.cql.model.NamespaceManager;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.IOException;

public class QICoreModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isQICoreModelIdentifier(ModelIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("QICore") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir/us/qicore"));
        }

        return modelIdentifier.getId().equals("QICore");
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (isQICoreModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            try {
                switch (localVersion) {
                    case "4.0.0":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.0.0.xml"));
                    case "4.1.0":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.0.xml"));
                    case "4.1.1":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.1.xml"));
                    case "5.0.0":
                    default:
                        return ModelInfoReaderFactory.getReader("application/xml").read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-5.0.0.xml"));
                }
            } catch (IOException e) {
                // Do not throw, allow other providers to resolve
            }
        }

        return null;
    }
}
