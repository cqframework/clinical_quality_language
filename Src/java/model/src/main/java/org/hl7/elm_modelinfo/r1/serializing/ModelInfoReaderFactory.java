package org.hl7.elm_modelinfo.r1.serializing;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ModelInfoReaderFactory {
    static ServiceLoader<ModelInfoReaderProvider> loader = ServiceLoader
            .load(ModelInfoReaderProvider.class);

    public static synchronized Iterator<ModelInfoReaderProvider> providers(boolean refresh) {
        if (refresh) {
            loader.reload();
        }

        return loader.iterator();
    }

    public static ModelInfoReader getReader(String contentType) {
        var providers = providers(false);
        if (providers.hasNext()) {
            return providers.next().create(contentType);
        }

        throw new RuntimeException("No ModelInfoReaderProviders found");
    }
}
