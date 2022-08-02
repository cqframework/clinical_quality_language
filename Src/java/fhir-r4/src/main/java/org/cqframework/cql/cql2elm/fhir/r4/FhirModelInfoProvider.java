package org.cqframework.cql.cql2elm.fhir.r4;

import org.cqframework.cql.cql2elm.ModelInfoProvider;
import org.cqframework.cql.cql2elm.NamespaceAware;
import org.cqframework.cql.cql2elm.NamespaceManager;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.IOException;

/**
 * Created by Bryn on 4/15/2016.
 */
public class FhirModelInfoProvider implements ModelInfoProvider, NamespaceAware {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isFHIRModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("FHIR") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir"));
        }

        return modelIdentifier.getId().equals("FHIR");
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (isFHIRModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            try {
                switch (localVersion) {
                    case "4.0.1":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("fhir-modelinfo-4.0.1.xml"));

                    // Do not throw, allow other providers to return the model if known
                    //default:
                    //    throw new IllegalArgumentException(String.format("Unknown version %s of the FHIR model.", localVersion));
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

