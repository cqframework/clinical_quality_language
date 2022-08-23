package org.cqframework.cql.cql2elm;

import java.util.Iterator;
import java.util.ServiceLoader;

public class LibrarySourceProviderFactory {

    static ServiceLoader<LibrarySourceProvider> loader = ServiceLoader
            .load(LibrarySourceProvider.class);

    public static Iterator<LibrarySourceProvider> providers(boolean refresh) {
        if (refresh) {
            loader.reload();
        }
        return loader.iterator();
    }
}
