package org.cqframework.cql.cql2elm.quick;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import java.io.InputStream;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.cql.model.NamespaceAware;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

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
            return modelIdentifier.getId().equals("FHIR")
                    && (modelIdentifier.getSystem() == null
                            || modelIdentifier.getSystem().equals("http://hl7.org/fhir"));
        }

        return modelIdentifier.getId().equals("FHIR");
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (isFHIRModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            var stream = getResource(localVersion);
            if (stream != null) {
                return ModelInfoReaderFactory.INSTANCE
                        .getReader("application/xml")
                        .read(buffered(asSource(stream)));
            }
        }

        return null;
    }

    private InputStream getResource(String localVersion) {
        switch (localVersion) {
            case "1.0.2":
                return FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.0.2.xml");

            case "1.4":
                return FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml");

            case "1.6":
                return FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.6.xml");

            case "1.8":
                return FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.8.xml");

            case "3.0.0":
            case "":
                return FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.0.xml");

            case "3.0.1":
                return FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.1.xml");

            case "3.2.0":
                return FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.2.0.xml");

            case "4.0.0":
                return FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.0.xml");

            case "4.0.1":
                return FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.1.xml");
        }

        return null;
    }
}
