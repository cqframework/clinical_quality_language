package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.Model;
import org.cqframework.cql.cql2elm.model.SystemModel;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Bryn on 12/29/2016.
 */
public class ModelManager {
    private NamespaceManager namespaceManager;
    private ModelInfoLoader modelInfoLoader;
    private final Map<String, Model> models = new HashMap<>();
    private final Set<String> loadingModels = new HashSet<>();
    private final Map<String, Model> modelsByUri = new HashMap<>();
    private boolean enableDefaultModelInfoLoading = true;

    public ModelManager() {
        namespaceManager = new NamespaceManager();
        initialize();
    }

    public ModelManager(boolean enableDefaultModelInfoLoading) {
        namespaceManager = new NamespaceManager();
        setEnableDefaultModelInfoLoading(enableDefaultModelInfoLoading);
        initialize();
    }

    public ModelManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
        initialize();
    }

    public ModelManager(NamespaceManager namespaceManager, boolean enableDefaultModelInfoLoading) {
        this.namespaceManager = namespaceManager;
        setEnableDefaultModelInfoLoading(enableDefaultModelInfoLoading);
        initialize();
    }

    private void initialize() {
        modelInfoLoader = new ModelInfoLoader(enableDefaultModelInfoLoading);
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

    public void setEnableDefaultModelInfoLoading(boolean enableDefaultModelInfoLoading) {
        this.enableDefaultModelInfoLoading = enableDefaultModelInfoLoading;
    }

    public void disableDefaultModelInfoLoading() {
        this.enableDefaultModelInfoLoading = false;
    }

    public ModelManager withDisabledDefaultModelInfoLoading() {
        disableDefaultModelInfoLoading();
        return this;
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

    private Model buildModel(VersionedIdentifier identifier) {
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
            }
            else {
                model = new Model(modelInfo, this);
            }
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not load model information for model %s, version %s.",
                    identifier.getId(), identifier.getVersion()));
        }
        finally {
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
        return resolveModel(new VersionedIdentifier().withId(modelName).withVersion(version));
    }

    public Model resolveModel(VersionedIdentifier modelIdentifier) {
        String modelPath = NamespaceManager.getPath(modelIdentifier.getSystem(), modelIdentifier.getId());
        Model model = models.get(modelPath);
        if (model == null) {
            model = buildModel(modelIdentifier);
            models.put(modelPath, model);
            modelsByUri.put(model.getModelInfo().getUrl(), model);
        }

        if (modelIdentifier.getVersion() != null && !modelIdentifier.getVersion().equals(model.getModelInfo().getVersion())) {
            throw new IllegalArgumentException(String.format("Could not load model information for model %s, version %s because version %s is already loaded.",
                    modelIdentifier.getId(), modelIdentifier.getVersion(), model.getModelInfo().getVersion()));
        }

        return model;
    }

    public Model resolveModelByUri(String namespaceUri) {
        Model model = modelsByUri.get(namespaceUri);
        if (model == null) {
            throw new IllegalArgumentException(String.format("Could not resolve model with namespace %s", namespaceUri));
        }

        return model;
    }
}
