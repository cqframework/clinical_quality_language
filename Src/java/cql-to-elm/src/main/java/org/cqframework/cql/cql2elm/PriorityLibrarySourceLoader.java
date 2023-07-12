package org.cqframework.cql.cql2elm;

import org.hl7.cql.model.NamespaceAware;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

/**
 * Used by LibraryManager to manage a set of library source providers that
 * resolve library includes within CQL. Package private since its not intended
 * to be used outside the context of the instantiating LibraryManager instance.
 */
public class PriorityLibrarySourceLoader implements LibrarySourceLoader, NamespaceAware, PathAware {
    private final List<LibrarySourceProvider> PROVIDERS = new ArrayList<>();
    private boolean initialized = false;

    @Override
    public void registerProvider(LibrarySourceProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider is null.");
        }

        if (provider instanceof NamespaceAware) {
            ((NamespaceAware)provider).setNamespaceManager(namespaceManager);
        }

        if (path != null && provider instanceof PathAware) {
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
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL);
    }

    @Override
    public InputStream getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {

        validateInput(libraryIdentifier, type);
        InputStream content = null;
        for (LibrarySourceProvider provider : getProviders()) {
            content = provider.getLibraryContent(libraryIdentifier, type);

            if (content != null) {
                return content;
            }
        }

        return null;
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

    private void validateInput(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (type == null) {
            throw new IllegalArgumentException("libraryContentType is null.");
        }

        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null.");
        }
    }
}
