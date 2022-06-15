package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
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

    private boolean isQDMModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("QDM") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("urn:healthit-gov"));
        }

        return modelIdentifier.getId().equals("QDM");
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
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
                e.printStackTrace();
            // Do not throw, allow other providers to resolve
            //    throw new IllegalArgumentException(String.format("Unknown version %s of the QDM model.", localVersion));
            }
        }

        return null;
    }
}
