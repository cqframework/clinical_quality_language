package org.cqframework.cql.elm.serializing;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ElmLibraryReaderFactory {

    static ServiceLoader<ElmLibraryReaderProvider> loader = ServiceLoader
            .load(ElmLibraryReaderProvider.class);

    public static Iterator<ElmLibraryReaderProvider> providers(boolean refresh) {
        if (refresh) {
            loader.reload();
        }
        return loader.iterator();
    }

    public static ElmLibraryReader getReader(String contentType) {
        if (providers(false).hasNext()) {
            return providers(false).next().create(contentType);
        }
        throw new RuntimeException("No ElmLibraryReaderProviders found");
    }
}
