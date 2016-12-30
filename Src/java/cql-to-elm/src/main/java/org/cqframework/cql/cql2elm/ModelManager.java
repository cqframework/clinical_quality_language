package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.Model;
import org.cqframework.cql.cql2elm.model.SystemModel;
import org.hl7.elm.r1.VersionedIdentifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bryn on 12/29/2016.
 */
public class ModelManager {
    private final Map<String, Model> models = new HashMap<>();

    private Model buildModel(VersionedIdentifier identifier) {
        Model model = null;
        try {
            ModelInfoProvider provider = ModelInfoLoader.getModelInfoProvider(identifier);
            if (identifier.getId().equals("System")) {
                model = new SystemModel(provider.load());
            }
            else {
                model = new Model(provider.load(), resolveModel("System"));
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not load model information for model %s, version %s.",
                    identifier.getId(), identifier.getVersion()));
        }

        return model;
    }

    public Model resolveModel(String modelName) {
        return resolveModel(modelName, null);
    }

    public Model resolveModel(String modelName, String version) {
        return resolveModel(new VersionedIdentifier().withId(modelName).withVersion(version));
    }

    public Model resolveModel(VersionedIdentifier modelIdentifier) {
        Model model = models.get(modelIdentifier.getId());
        if (model == null) {
            model = buildModel(modelIdentifier);
            models.put(modelIdentifier.getId(), model);
        }

        if (modelIdentifier.getVersion() != null && !modelIdentifier.getVersion().equals(model.getModelInfo().getVersion())) {
            throw new IllegalArgumentException(String.format("Could not load model information for model %s, version %s because version %s is already loaded.",
                    modelIdentifier.getId(), modelIdentifier.getVersion(), model.getModelInfo().getVersion()));
        }

        return model;
    }
}
