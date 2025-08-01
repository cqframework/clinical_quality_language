package org.cqframework.cql.cql2elm.quick;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.hl7.elm_modelinfo.r1.serializing.XmlModelInfoReaderKt.parseModelInfoXml;

import java.io.InputStream;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm_modelinfo.r1.ModelInfo;

public class UsCoreModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isUSCoreModelIdentifier(ModelIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("USCore")
                    && (modelIdentifier.getSystem() == null
                            || modelIdentifier.getSystem().equals("http://hl7.org/fhir/us/core"));
        }

        return modelIdentifier.getId().equals("USCore");
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (isUSCoreModelIdentifier(modelIdentifier)) {
            String localVersion = modelIdentifier.getVersion() == null ? "" : modelIdentifier.getVersion();
            var stream = getResource(localVersion);
            if (stream != null) {
                return parseModelInfoXml(buffered(asSource(stream)));
            }
        }

        return null;
    }

    private InputStream getResource(String localVersion) {
        switch (localVersion) {
            case "3.1.0":
                return QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.0.xml");
            case "3.1.1":
                return QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.1.xml");
            case "6.1.0":
            case "":
                return QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-6.1.0.xml");
        }
        return null;
    }
}
