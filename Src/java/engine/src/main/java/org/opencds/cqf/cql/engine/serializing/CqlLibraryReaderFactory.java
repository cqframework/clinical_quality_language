package org.opencds.cqf.cql.engine.serializing;

import java.util.Iterator;
import java.util.ServiceLoader;

public class CqlLibraryReaderFactory {

    private CqlLibraryReaderFactory() {
    }

    public static Iterator<CqlLibraryReaderProvider> providers(boolean refresh) {
        var loader = ServiceLoader
                .load(CqlLibraryReaderProvider.class);
        if (refresh) {
            loader.reload();
        }

        return loader.iterator();
    }

    public static CqlLibraryReader getReader(String contentType) {
        var providers = providers(false);
        if (providers.hasNext()) {
            CqlLibraryReaderProvider p = providers.next();
            if (providers.hasNext()) {
                throw new RuntimeException(String.join(" ",
                        "Multiple CqlLibraryReaderProviders found on the classpath.",
                        "You need to remove a reference to either the 'engine.jackson' or the 'engine.jaxb' package"));
            }

            return p.create(contentType);
        }

        throw new RuntimeException(String.join(" ",
                "No CqlLibraryReaderProviders found on the classpath.",
                "You need to add a reference to one of the 'engine.jackson' or 'engine.jaxb' packages,",
                "or provide your own implementation."));
    }
}
