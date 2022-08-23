package org.cqframework.cql.cql2elm;

import org.hl7.cql.model.NamespaceAware;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Used by LibraryManager to manage a set of library source providers that
 * resolve library includes within CQL. Package private since its not intended
 * to be used outside the context of the instantiating LibraryManager instance.
 */
class DefaultLibrarySourceLoader implements LibrarySourceLoader, NamespaceAware, PathAware {
    private final List<LibrarySourceProvider> PROVIDERS = new ArrayList<>();
    boolean initialized = false;

    @Override
    public void registerProvider(LibrarySourceProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider is null.");
        }

        if (provider instanceof NamespaceAware) {
            ((NamespaceAware)provider).setNamespaceManager(namespaceManager);
        }

        if (provider instanceof PathAware) {
            ((PathAware)provider).setPath(path);
        }

        PROVIDERS.add(provider);
    }

    private Path path;
    public void setPath(Path path) {
        if (path == null || ! path.toFile().isDirectory()) {
            throw new IllegalArgumentException(String.format("path '%s' is not a valid directory", path));
        }

        this.path = path;

        for (LibrarySourceProvider provider : getProviders()) {
            if (provider instanceof PathAware) {
                ((PathAware)provider).setPath(path);
            }
        }
    }

    @Override
    public void clearProviders() {
        PROVIDERS.clear();
        initialized = false;
    }

    private List<LibrarySourceProvider> getProviders() {
        if (!initialized) {
            initialized = true;
            for (Iterator<LibrarySourceProvider> it = LibrarySourceProviderFactory.providers(false); it.hasNext(); ) {
                LibrarySourceProvider provider = it.next();
                registerProvider(provider);
            }
        }

        return PROVIDERS;
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
        for (LibrarySourceProvider provider : getProviders()) {
            InputStream localSource = provider.getLibrarySource(libraryIdentifier);
            if (localSource != null) {
                if (source != null) {
                    throw new IllegalArgumentException(String.format("Multiple sources found for library %s, version %s.",
                            libraryIdentifier.getId(), libraryIdentifier.getVersion()));
                }

                source = localSource;
            }
        }

        if (source == null) {
            throw new IllegalArgumentException(String.format("Could not load source for library %s, version %s.",
                    libraryIdentifier.getId(), libraryIdentifier.getVersion()));
        }

        return source;
    }

    private NamespaceManager namespaceManager;

    @Override
    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;

        for (LibrarySourceProvider provider : getProviders()) {
            if (provider instanceof NamespaceAware) {
                ((NamespaceAware)provider).setNamespaceManager(namespaceManager);
            }
        }
    }
}
