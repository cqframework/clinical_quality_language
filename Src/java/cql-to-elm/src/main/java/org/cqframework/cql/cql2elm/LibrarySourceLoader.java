package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LibrarySourceLoader {
    private static final List<LibrarySourceProvider> PROVIDERS = new ArrayList<>();

    public static void registerProvider(LibrarySourceProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider is null.");
        }

        PROVIDERS.add(provider);
    }

    public static void clearProviders() {
        PROVIDERS.clear();
    }

    public static InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null.");
        }

        InputStream source = null;
        for (LibrarySourceProvider provider : PROVIDERS) {
            InputStream localSource = provider.getLibrarySource(libraryIdentifier);
            if (localSource != null) {
                if (source != null) {
                    throw new IllegalArgumentException(String.format("Multiple sources found for library %s, version %s.",
                            libraryIdentifier.getId(), libraryIdentifier.getVersion()));
                }

                source = localSource;
            }
        }

        return source;
    }
}
