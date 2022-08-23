package org.cqframework.cql.elm.serializing;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ElmLibraryWriterFactory {

    static ServiceLoader<ElmLibraryWriterProvider> loader = ServiceLoader
            .load(ElmLibraryWriterProvider.class);

    public static Iterator<ElmLibraryWriterProvider> providers(boolean refresh) {
        if (refresh) {
            loader.reload();
        }
        return loader.iterator();
    }

    public static ElmLibraryWriter getWriter(String contentType) {
        if (providers(false).hasNext()) {
            return providers(false).next().create(contentType);
        }
        throw new RuntimeException("No ElmLibraryWriterProviders found");
    }
}
