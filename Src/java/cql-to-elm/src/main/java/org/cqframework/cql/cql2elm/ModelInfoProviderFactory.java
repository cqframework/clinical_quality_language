package org.cqframework.cql.cql2elm;

import org.hl7.cql.model.ModelInfoProvider;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ModelInfoProviderFactory {
    private ModelInfoProviderFactory() {}

    public static Iterator<ModelInfoProvider> providers(boolean refresh) {
        var loader = ServiceLoader.load(ModelInfoProvider.class);
        if (refresh) {
            loader.reload();
        }

        return loader.iterator();
    }
}
