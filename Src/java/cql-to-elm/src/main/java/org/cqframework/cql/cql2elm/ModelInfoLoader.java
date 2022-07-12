package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModelInfoLoader implements NamespaceAware, PathAware {

    private Path path;

    private NamespaceManager namespaceManager;

    private final List<ModelInfoProvider> providers = new ArrayList<>();

    private boolean initialized = false;

    public ModelInfoLoader() {
    }

    private List<ModelInfoProvider> getProviders() {
        if (!initialized) {
            initialized = true;
            for (Iterator<ModelInfoProvider> it = ModelInfoProviderFactory.providers(false); it.hasNext(); ) {
                ModelInfoProvider provider = it.next();
                registerModelInfoProvider(provider);
            }
        }
        return providers;
    }

    public ModelInfo getModelInfo(VersionedIdentifier modelIdentifier) {
        checkModelIdentifier(modelIdentifier);

        ModelInfo modelInfo = null;

        for (ModelInfoProvider provider : getProviders()) {
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

        if (namespaceManager != null) {
            if (provider instanceof NamespaceAware) {
                ((NamespaceAware)provider).setNamespaceManager(namespaceManager);
            }
        }

        if (path != null) {
            if (provider instanceof PathAware) {
                ((PathAware)provider).setPath(path);
            }
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
        initialized = false;
    }

    private void checkModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier == null) {
            throw new IllegalArgumentException("modelIdentifier is null.");
        }

        if (modelIdentifier.getId() == null || modelIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("modelIdentifier Id is null or empty.");
        }
    }

    @Override
    public void setNamespaceManager(NamespaceManager namespaceManager) {
        namespaceManager = namespaceManager;

        for (ModelInfoProvider provider : getProviders()) {
            if (provider instanceof NamespaceAware) {
                ((NamespaceAware)provider).setNamespaceManager(namespaceManager);
            }
        }
    }

    public void setPath(Path path) {
        if (path == null || ! path.toFile().isDirectory()) {
            throw new IllegalArgumentException(String.format("path '%s' is not a valid directory", path));
        }

        this.path = path;

        for (ModelInfoProvider provider : getProviders()) {
            if (provider instanceof PathAware) {
                ((PathAware)provider).setPath(path);
            }
        }
    }
}
