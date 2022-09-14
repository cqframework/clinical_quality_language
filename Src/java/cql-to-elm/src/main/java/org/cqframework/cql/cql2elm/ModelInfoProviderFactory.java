package org.cqframework.cql.cql2elm;

import org.hl7.cql.model.ModelInfoProvider;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ModelInfoProviderFactory {

    static ServiceLoader<ModelInfoProvider> loader = ServiceLoader
            .load(ModelInfoProvider.class);

    public static synchronized Iterator<ModelInfoProvider> providers(boolean refresh) {
        if (refresh) {
            loader.reload();
        }

        return loader.iterator();
    }
}
