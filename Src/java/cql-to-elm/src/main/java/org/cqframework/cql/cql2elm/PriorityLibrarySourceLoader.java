package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Used by LibraryManager to manage a set of library source providers that
 * resolve library includes within CQL. Package private since its not intended
 * to be used outside the context of the instantiating LibraryManager instance.
 */
public class PriorityLibrarySourceLoader implements LibrarySourceLoader {
    private final List<LibrarySourceProvider> PROVIDERS = new ArrayList<>();

    @Override
    public void registerProvider(LibrarySourceProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider is null.");
        }

        PROVIDERS.add(provider);
    }

    @Override
    public void clearProviders() {
        PROVIDERS.clear();
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
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
                return localSource;
            }
        }

        throw new IllegalArgumentException(String.format("Could not load source for library %s, version %s.",
                libraryIdentifier.getId(), libraryIdentifier.getVersion()));
    }
}
