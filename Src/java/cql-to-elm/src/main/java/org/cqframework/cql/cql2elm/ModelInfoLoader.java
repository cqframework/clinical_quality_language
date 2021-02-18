package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelInfoLoader {

    private final List<ModelInfoProvider> providers = new ArrayList<>();

    public ModelInfoLoader() {
        registerWellKnownModelInfoProviders();
    }

    private void registerWellKnownModelInfoProviders() {
        registerModelInfoProvider(new SystemModelInfoProvider());
        registerModelInfoProvider(new QuickModelInfoProvider());
        registerModelInfoProvider(new QdmModelInfoProvider());
        registerModelInfoProvider(new FhirModelInfoProvider());
        registerModelInfoProvider(new UsCoreModelInfoProvider());
        registerModelInfoProvider(new QICoreModelInfoProvider());
    }

    public ModelInfo getModelInfo(VersionedIdentifier modelIdentifier) {
        ModelInfo modelInfo = null;

        for (ModelInfoProvider provider : providers) {
            modelInfo = provider.load(modelIdentifier);
            if (modelInfo != null) {
                break;
            }
        }

        if (modelInfo == null) {
            throw new IllegalArgumentException(String.format("Could not resolve model info provider for model %s, version %s.",
                    modelIdentifier.getSystem() == null ? modelIdentifier.getId() : NamespaceManager.getPath(modelIdentifier.getSystem(), modelIdentifier.getId()),
                    modelIdentifier.getVersion()));
        }

        return modelInfo;
    }

    public void registerModelInfoProvider(ModelInfoProvider provider) {
        registerModelInfoProvider(provider, false);
    }

    public void registerModelInfoProvider(ModelInfoProvider provider, boolean priority) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider is null");
        }

        if (priority) {
            providers.add(0, provider);
        }
        else {
            providers.add(provider);
        }
    }

    public void unregisterModelInfoProvider(ModelInfoProvider provider) {
        providers.remove(provider);
    }

    public void clearModelInfoProviders() {
        providers.clear();
    }

    private void checkModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier == null) {
            throw new IllegalArgumentException("modelIdentifier is null.");
        }

        if (modelIdentifier.getId() == null || modelIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("modelIdentifier Id is null.");
        }
    }
}
