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

    public ModelManager() {
        namespaceManager = new NamespaceManager();
        initialize();
    }

    public ModelManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
        initialize();
    }

    private void initialize() {
        modelInfoLoader = new ModelInfoLoader();
    }

    public NamespaceManager getNamespaceManager() {
        return this.namespaceManager;
    }

    public ModelInfoLoader getModelInfoLoader() {
        return this.modelInfoLoader;
    }

    public void registerWellKnownNamespaces() {
        if (namespaceManager != null) {
            namespaceManager.ensureNamespaceRegistered(new NamespaceInfo("FHIR", "http://hl7.org/fhir"));
            namespaceManager.ensureNamespaceRegistered(new NamespaceInfo("ecqi.healthit.gov", "urn:healthit-gov"));
            namespaceManager.ensureNamespaceRegistered(new NamespaceInfo("hl7.fhir.us.core", "http://hl7.org/fhir/us/core"));
            namespaceManager.ensureNamespaceRegistered(new NamespaceInfo("hl7.fhir.us.qicore", "http://hl7.org/fhir/us/qicore"));
        }
    }

    /*
    If we are using namespaces, for well-known models, return the namespace name for the model
     */
    public String getWellKnownNamespaceName(String unqualifiedIdentifier) {
        if (unqualifiedIdentifier == null) {
            return null;
        }

        switch (unqualifiedIdentifier) {
            case "FHIR": return "FHIR"; // NOTE: Should probably be hl7.fhir.r4.core, but that is version specific, and FHIR has the same name across model info versions
            case "QDM": return "ecqi.healthit.gov";
            case "USCore": return "hl7.fhir.us.core";
            case "QICore": return "hl7.fhir.us.qicore";
        }

        return null;
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
        }

        if (modelIdentifier.getVersion() != null && !modelIdentifier.getVersion().equals(model.getModelInfo().getVersion())) {
            throw new IllegalArgumentException(String.format("Could not load model information for model %s, version %s because version %s is already loaded.",
                    modelIdentifier.getId(), modelIdentifier.getVersion(), model.getModelInfo().getVersion()));
        }

        return model;
    }
}
