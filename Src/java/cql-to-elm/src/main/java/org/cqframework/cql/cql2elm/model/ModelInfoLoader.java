package org.cqframework.cql.cql2elm.model;

import org.hl7.elm.r1.VersionedIdentifier;

import java.util.HashMap;
import java.util.Map;

public class ModelInfoLoader {

    private static final Map<VersionedIdentifier, ModelInfoProvider> PROVIDERS =
            new HashMap<VersionedIdentifier, ModelInfoProvider>();

    static {
        registerModelInfoProvider(new VersionedIdentifier().withId("System").withVersion("1"), new SystemModelInfoProvider());
        registerModelInfoProvider(new VersionedIdentifier().withId("QUICK").withVersion("1"), new QuickModelInfoProvider());
    }

    public static ModelInfoProvider getModelInfoProvider(VersionedIdentifier modelIdentifier) {
        checkModelIdentifier(modelIdentifier);

        ModelInfoProvider provider = PROVIDERS.get(modelIdentifier);
        if (provider == null) {
            throw new IllegalArgumentException(String.format("Could not resolve model info provider for model %s, version %s.",
                    modelIdentifier.getId(), modelIdentifier.getVersion()));
        }

        return provider;
    }

    public static void registerModelInfoProvider(VersionedIdentifier modelIdentifier, ModelInfoProvider provider) {
        checkModelIdentifier(modelIdentifier);

        if (provider == null) {
            throw new IllegalArgumentException("Provider is null");
        }

        PROVIDERS.put(modelIdentifier, provider);

        if (modelIdentifier.getVersion() != null) {
            VersionedIdentifier versionlessIdentifier = new VersionedIdentifier().withId(modelIdentifier.getId());
            PROVIDERS.put(versionlessIdentifier, provider);
        }
    }

    private static void checkModelIdentifier(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier == null) {
            throw new IllegalArgumentException("modelIdentifier is null.");
        }

        if (modelIdentifier.getId() == null || modelIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("modelIdentifier Id is null.");
        }
    }
}
