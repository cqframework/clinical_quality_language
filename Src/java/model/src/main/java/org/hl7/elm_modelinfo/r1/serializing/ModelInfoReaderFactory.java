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
            ModelInfoReaderProvider p = providers.next();
            if (providers.hasNext()) {
                throw new RuntimeException(String.join(" ",
                "Multiple ModelInfoReaderProviders found on the classpath.",
                "You need to remove a reference to either the 'model-jackson' or the 'model-jaxb' package"));
            }

            return p.create(contentType);
        }

        throw new RuntimeException(String.join(" ",
                "No ModelInfoReaderProviders found on the classpath.",
                "You need to add a reference to one of the 'model-jackson' or 'model-jaxb' packages,",
                "or provide your own implementation."));
    }
}
