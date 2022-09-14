package org.cqframework.cql.elm.serializing;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ElmLibraryWriterFactory {

    static ServiceLoader<ElmLibraryWriterProvider> loader = ServiceLoader
            .load(ElmLibraryWriterProvider.class);

    public static synchronized Iterator<ElmLibraryWriterProvider> providers(boolean refresh) {
        if (refresh) {
            loader.reload();
        }

        return loader.iterator();
    }

    public static ElmLibraryWriter getWriter(String contentType) {
        var providers = providers(false);
        if (providers.hasNext()) {
            return providers.next().create(contentType);
        }

        throw new RuntimeException("No ElmLibraryWriterProviders found");
    }
}
