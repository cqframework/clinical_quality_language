package org.cqframework.cql.cql2elm;

import java.util.Iterator;
import java.util.ServiceLoader;

public class LibrarySourceProviderFactory {
    private LibrarySourceProviderFactory() {}

    public static Iterator<LibrarySourceProvider> providers(boolean refresh) {
        var loader = ServiceLoader.load(LibrarySourceProvider.class);
        if (refresh) {
            loader.reload();
        }

        return loader.iterator();
    }
}
