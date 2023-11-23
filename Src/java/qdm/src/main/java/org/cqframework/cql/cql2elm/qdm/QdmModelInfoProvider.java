package org.cqframework.cql.cql2elm.qdm;

import org.hl7.cql.model.NamespaceAware;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.IOException;

/**
 * Created by Bryn on 2/3/2016.
 */
public class QdmModelInfoProvider implements ModelInfoProvider, NamespaceAware {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isQDMModelIdentifier(ModelIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("QDM") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("urn:healthit-gov"));
        }

        return modelIdentifier.getId().equals("QDM");
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (isQDMModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            try {
                switch (localVersion) {
                    case "4.1.2":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo.xml"));
                    case "4.2":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.2.xml"));
                    case "4.3":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.3.xml"));
                    case "5.0":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.xml"));
                    case "5.0.1":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.1.xml"));
                    case "5.0.2":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.2.xml"));
                    case "5.3":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.3.xml"));
                    case "5.4":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.4.xml"));
                    case "5.5":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.5.xml"));
                    case "5.6":
                    case "":
                        return ModelInfoReaderFactory.getReader("application/xml").read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.6.xml"));
                }
            } catch (IOException e) {
                // Do not throw, allow other providers to resolve
            }
        }

        return null;
    }
}
