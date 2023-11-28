package org.cqframework.cql.cql2elm.quick;

import org.hl7.cql.model.NamespaceAware;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
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

    private boolean isFHIRModelIdentifier(ModelIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("FHIR") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("http://hl7.org/fhir"));
        }

        return modelIdentifier.getId().equals("FHIR");
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (isFHIRModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            try {
                switch (localVersion) {
                    case "1.0.2":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.0.2.xml"));

                    case "1.4":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml"));

                    case "1.6":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.6.xml"));

                    case "1.8":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.8.xml"));

                    case "3.0.0":
                    case "":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.0.xml"));

                    case "3.0.1":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.1.xml"));

                    case "3.2.0":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.2.0.xml"));

                    case "4.0.0":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.0.xml"));

                    case "4.0.1":
                        return ModelInfoReaderFactory.getReader("application/xml").read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.1.xml"));

                    // Do not throw, allow other providers to return the model if known
                    //default:
                    //    throw new IllegalArgumentException(String.format("Unknown version %s of the FHIR model.", localVersion));
                }
            } catch (IOException e) {
                // Do not throw, allow other providers to resolve
            }
        }

        return null;
    }
}

