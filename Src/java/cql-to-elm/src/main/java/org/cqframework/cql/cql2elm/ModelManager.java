package org.cqframework.cql.cql2elm;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.cqframework.cql.cql2elm.model.Model;
import org.cqframework.cql.cql2elm.model.SystemModel;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm_modelinfo.r1.ModelInfo;

/**
 * Created by Bryn on 12/29/2016.
 */
public class ModelManager {
    private final NamespaceManager namespaceManager;
    private Path path;
    private ModelInfoLoader modelInfoLoader;
    private final Map<String, Model> models = new HashMap<>();
    private final Set<String> loadingModels = new HashSet<>();
    private final Map<String, Model> modelsByUri = new HashMap<>();
    private final Map<ModelIdentifier, Model> globalCache;
    private boolean enableDefaultModelInfoLoading = true;

    public ModelManager() {
        this.namespaceManager = new NamespaceManager();
        this.globalCache = new ConcurrentHashMap<>();
        initialize();
    }

    /**
     * @param globalCache cache for Models by ModelIdentifier. Expected to be thread-safe.
     */
    public ModelManager(Map<ModelIdentifier, Model> globalCache) {
        requireNonNull(globalCache, "globalCache can not be null.");

        this.namespaceManager = new NamespaceManager();
        this.globalCache = globalCache;
        initialize();
    }

    public ModelManager(Path path) {
        this.namespaceManager = new NamespaceManager();
        this.globalCache = new ConcurrentHashMap<>();
        this.path = path;
        initialize();
    }

    public ModelManager(Path path, Map<ModelIdentifier, Model> globalCache) {
        requireNonNull(globalCache, "globalCache can not be null.");

        this.namespaceManager = new NamespaceManager();
        this.globalCache = globalCache;
        this.path = path;
        initialize();
    }

    public ModelManager(boolean enableDefaultModelInfoLoading) {
        this.namespaceManager = new NamespaceManager();
        this.globalCache = new ConcurrentHashMap<>();
        this.enableDefaultModelInfoLoading = enableDefaultModelInfoLoading;
        initialize();
    }

    public ModelManager(boolean enableDefaultModelInfoLoading, Map<ModelIdentifier, Model> globalCache) {
        requireNonNull(globalCache, "globalCache can not be null.");
        this.namespaceManager = new NamespaceManager();
        this.globalCache = globalCache;
        this.enableDefaultModelInfoLoading = enableDefaultModelInfoLoading;
        initialize();
    }

    public ModelManager(boolean enableDefaultModelInfoLoading, Path path) {
        this.namespaceManager = new NamespaceManager();
        this.globalCache = new ConcurrentHashMap<>();
        this.path = path;
        this.enableDefaultModelInfoLoading = enableDefaultModelInfoLoading;
        initialize();
    }

    public ModelManager(boolean enableDefaultModelInfoLoading, Path path, Map<ModelIdentifier, Model> globalCache) {
        requireNonNull(globalCache, "globalCache can not be null.");
        this.namespaceManager = new NamespaceManager();
        this.globalCache = globalCache;
        this.path = path;
        this.enableDefaultModelInfoLoading = enableDefaultModelInfoLoading;
        initialize();
    }

    public ModelManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
        this.globalCache = new ConcurrentHashMap<>();
        initialize();
    }

    public ModelManager(NamespaceManager namespaceManager, Map<ModelIdentifier, Model> globalCache) {
        requireNonNull(globalCache, "globalCache can not be null.");
        this.namespaceManager = namespaceManager;
        this.globalCache = globalCache;
        initialize();
    }

    public ModelManager(NamespaceManager namespaceManager, Path path) {
        this.namespaceManager = namespaceManager;
        this.globalCache = new ConcurrentHashMap<>();
        this.path = path;
        initialize();
    }

    public ModelManager(NamespaceManager namespaceManager, Path path, Map<ModelIdentifier, Model> globalCache) {
        requireNonNull(globalCache, "globalCache can not be null.");
        this.namespaceManager = namespaceManager;
        this.globalCache = globalCache;
        this.path = path;
        initialize();
    }

    public ModelManager(NamespaceManager namespaceManager, boolean enableDefaultModelInfoLoading) {
        this.namespaceManager = namespaceManager;
        this.globalCache = new ConcurrentHashMap<>();
        this.enableDefaultModelInfoLoading = enableDefaultModelInfoLoading;
        initialize();
    }

    public ModelManager(
            NamespaceManager namespaceManager,
            boolean enableDefaultModelInfoLoading,
            Map<ModelIdentifier, Model> globalCache) {
        requireNonNull(globalCache, "globalCache can not be null.");
        this.namespaceManager = namespaceManager;
        this.globalCache = globalCache;
        this.enableDefaultModelInfoLoading = enableDefaultModelInfoLoading;
        initialize();
    }

    public ModelManager(NamespaceManager namespaceManager, boolean enableDefaultModelInfoLoading, Path path) {
        this.namespaceManager = namespaceManager;
        this.globalCache = new ConcurrentHashMap<>();
        this.path = path;
        this.enableDefaultModelInfoLoading = enableDefaultModelInfoLoading;
        initialize();
    }

    public ModelManager(
            NamespaceManager namespaceManager,
            boolean enableDefaultModelInfoLoading,
            Path path,
            Map<ModelIdentifier, Model> globalCache) {
        requireNonNull(globalCache, "globalCache can not be null.");
        this.namespaceManager = namespaceManager;
        this.globalCache = globalCache;
        this.path = path;
        this.enableDefaultModelInfoLoading = enableDefaultModelInfoLoading;
        initialize();
    }

    private void initialize() {
        modelInfoLoader = new ModelInfoLoader();
        modelInfoLoader.setNamespaceManager(namespaceManager);
        if (path != null) {
            modelInfoLoader.setPath(path);
        }
    }

    public NamespaceManager getNamespaceManager() {
        return this.namespaceManager;
    }

    public ModelInfoLoader getModelInfoLoader() {
        return this.modelInfoLoader;
    }

    public boolean isDefaultModelInfoLoadingEnabled() {
        return enableDefaultModelInfoLoading;
    }

    /**
     * The global cache is by @{org.hl7.cql.model.ModelIdentifier}, while the local cache is by name. This is because the translator expects the ModelManager to only permit loading
     * of a single version of a given Model in a single translation context, while the global cache is for all versions of Models
     */
    public Map<ModelIdentifier, Model> getGlobalCache() {
        return this.globalCache;
    }

    /*
    A "well-known" model name is one that is allowed to resolve without a namespace in a namespace-aware context
     */
    public boolean isWellKnownModelName(String unqualifiedIdentifier) {
        if (unqualifiedIdentifier == null) {
            return false;
        }

        switch (unqualifiedIdentifier) {
            case "FHIR":
            case "QDM":
            case "USCore":
            case "QICore":
            case "QUICK":
                return true;
            default:
                return false;
        }
    }

    private Model buildModel(ModelIdentifier identifier) {
        Model model = null;
        if (identifier == null) {
            throw new IllegalArgumentException("Model identifier is required");
        }
        if (identifier.getId() == null || identifier.getId().equals("")) {
            throw new IllegalArgumentException("Model identifier Id is required");
        }
        String modelPath = NamespaceManager.getPath(identifier.getSystem(), identifier.getId());
        pushLoading(modelPath);
        try {
            ModelInfo modelInfo = modelInfoLoader.getModelInfo(identifier);
            if (identifier.getId().equals("System")) {
                model = new SystemModel(modelInfo);
            } else {
                model = new Model(modelInfo, this);
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format(
                    "Could not load model information for model %s, version %s.",
                    identifier.getId(), identifier.getVersion()));
        } finally {
            popLoading(modelPath);
        }

        return model;
    }

    private void pushLoading(String modelId) {
        if (loadingModels.contains(modelId)) {
            throw new IllegalArgumentException(String.format("Circular model reference %s", modelId));
        }
        loadingModels.add(modelId);
    }

    private void popLoading(String modelId) {
        loadingModels.remove(modelId);
    }

    public Model resolveModel(String modelName) {
        return resolveModel(modelName, null);
    }

    public Model resolveModel(String modelName, String version) {
        return resolveModel(new ModelIdentifier().withId(modelName).withVersion(version));
    }

    /**
     * @param modelIdentifier the identifier of the model to resolve
     * @return the model
     * @throws IllegalArgumentException if an attempt to resolve multiple versions of the same model is made or if the model that resolved is not compatible with the requested version
     */
    public Model resolveModel(ModelIdentifier modelIdentifier) {
        String modelPath = NamespaceManager.getPath(modelIdentifier.getSystem(), modelIdentifier.getId());
        Model model = models.get(modelPath);
        if (model != null) {
            checkModelVersion(modelIdentifier, model);
        }

        if (model == null && this.globalCache.containsKey(modelIdentifier)) {
            model = this.globalCache.get(modelIdentifier);
            models.put(modelPath, model);
            modelsByUri.put(model.getModelInfo().getUrl(), model);
        }

        if (model == null) {
            model = buildModel(modelIdentifier);
            this.globalCache.put(modelIdentifier, model);
            checkModelVersion(modelIdentifier, model);
            models.put(modelPath, model);
            modelsByUri.put(model.getModelInfo().getUrl(), model);
        }

        return model;
    }

    private void checkModelVersion(ModelIdentifier modelIdentifier, Model model) {
        if (modelIdentifier.getVersion() != null
                && !modelIdentifier.getVersion().equals(model.getModelInfo().getVersion())) {
            throw new IllegalArgumentException(String.format(
                    "Could not load model information for model %s, version %s because version %s is already loaded.",
                    modelIdentifier.getId(),
                    modelIdentifier.getVersion(),
                    model.getModelInfo().getVersion()));
        }
    }

    public Model resolveModelByUri(String namespaceUri) {
        Model model = modelsByUri.get(namespaceUri);
        if (model == null) {
            throw new IllegalArgumentException(
                    String.format("Could not resolve model with namespace %s", namespaceUri));
        }

        return model;
    }
}
