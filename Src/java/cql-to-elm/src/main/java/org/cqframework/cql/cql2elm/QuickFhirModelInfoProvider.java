package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.io.IOException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Created by Bryn on 4/15/2016.
 */
public class QuickFhirModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isQuickFhirModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("QUICKFHIR") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir"));
        }

        return modelIdentifier.getId().equals("QUICKFHIR");
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (isQuickFhirModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            try {
                switch (localVersion) {
                    case "3.0.1":
                    case "":
                        return JacksonXML.readValue(QuickFhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quickfhir-modelinfo-3.0.1.xml"),
                                ModelInfo.class);
    
                    //default:
                    //    throw new IllegalArgumentException(String.format("Unknown version %s of the QUICKFHIR model.", localVersion));
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

