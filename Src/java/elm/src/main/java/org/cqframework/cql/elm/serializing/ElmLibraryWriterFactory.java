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
            ElmLibraryWriterProvider p = providers.next();
            if (providers.hasNext()) {
                throw new RuntimeException(String.join(" ",
                "Multiple ElmLibraryWriterProviders found on the classpath.",
                "You need to remove a reference to either the 'elm-jackson' or the 'elm-jaxb' package"));
            }

            return p.create(contentType);
        }

        throw new RuntimeException(String.join(" ",
                "No ElmLibraryWriterProviders found on the classpath.",
                "You need to add a reference to one of the 'elm-jackson' or 'elm-jaxb' packages,",
                "or provide your own implementation."));
    }
}
