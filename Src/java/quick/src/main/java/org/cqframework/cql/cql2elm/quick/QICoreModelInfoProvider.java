package org.cqframework.cql.cql2elm.quick;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import java.io.InputStream;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

public class QICoreModelInfoProvider implements ModelInfoProvider {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isQICoreModelIdentifier(ModelIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("QICore")
                    && (modelIdentifier.getSystem() == null
                            || modelIdentifier.getSystem().equals("http://hl7.org/fhir/us/qicore"));
        }

        return modelIdentifier.getId().equals("QICore");
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (isQICoreModelIdentifier(modelIdentifier)) {
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
            case "4.0.0":
                return QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.0.0.xml");
            case "4.1.0":
                return QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.0.xml");
            case "4.1.1":
                return QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.1.xml");
            case "5.0.0":
                return QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-5.0.0.xml");
            case "6.0.0":
            default:
                return QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-6.0.0.xml");
        }
    }
}
