package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SystemModelInfoProvider implements ModelInfoProviderExt {
    private NamespaceManager namespaceManager;

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    private boolean isSystemModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (namespaceManager != null && namespaceManager.hasNamespaces()) {
            return modelIdentifier.getId().equals("System") &&
                    (modelIdentifier.getSystem() == null || modelIdentifier.getSystem().equals("urn:hl7-org:elm-types:r1"));
        }

        return modelIdentifier.getId().equals("System");
    }

    @Override
    public ModelInfo load(VersionedIdentifier modelIdentifier, ModelManager.ModelInfoFormat modelInfoFormat) {
        if (modelInfoFormat.equals(ModelManager.ModelInfoFormat.XML)) {
            return load(modelIdentifier);
        } else if (modelInfoFormat.equals(ModelManager.ModelInfoFormat.JXSON)) {
            ModelInfo modelInfo = null;
            try {
                InputStream is = SystemModelInfoProvider.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.json");
                InputStreamReader reader = new InputStreamReader(is);
                modelInfo = CqlTranslator.deserializeModelInfo(reader);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return modelInfo;
        }
        return null;
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (isSystemModelIdentifier(modelIdentifier)) {
            return JAXB.unmarshal(SystemModelInfoProvider.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"),
                    ModelInfo.class);
        }

        return null;
    }
}
