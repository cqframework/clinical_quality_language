package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

/**
 * Used by LibraryManager to manage a set of library source providers that
 * resolve library includes within CQL. Package private since its not intended
 * to be used outside the context of the instantiating LibraryManager instance.
 */
public class PriorityLibrarySourceLoader implements LibrarySourceLoaderExt, NamespaceAware, PathAware {
    private final List<LibrarySourceProvider> PROVIDERS = new ArrayList<>();
    private Set<LibraryContentType> supportedTypes = new HashSet<>();

    private boolean initialized = false;

    @Override
    public void registerProvider(LibrarySourceProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider is null.");
        }

        if(supportedTypes == null) {
            supportedTypes = new HashSet<>();
        }

        if (provider instanceof LibrarySourceProviderExt) {
            LibrarySourceProviderExt providerExt = (LibrarySourceProviderExt) provider;
            supportedTypes.add(providerExt.getLibrarySourceType());
        }

        if (provider instanceof NamespaceAware) {
            ((NamespaceAware)provider).setNamespaceManager(namespaceManager);
        }

        if (path != null) {
            if (provider instanceof PathAware) {
                ((PathAware)provider).setPath(path);
            }
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
    public Set<LibraryContentType> getSupportedContentTypes() {
        return supportedTypes;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier, LibraryContentType type) {

        validateInput(libraryIdentifier, type);
        InputStream source = null;
        for (LibrarySourceProvider provider : getProviders()) {
            if (provider instanceof LibrarySourceProviderExt) {
                LibrarySourceProviderExt providerExt = (LibrarySourceProviderExt) provider;
                if (providerExt.isLibrarySourceAvailable(libraryIdentifier, type)) {
                    source = providerExt.getLibrarySource(libraryIdentifier, type);
                }
            } else {
                source = provider.getLibrarySource(libraryIdentifier);
            }
            if (source != null) {
                return source;
            }
        }

        throw new IllegalArgumentException(String.format("Could not load source for library %s, version %s.",
                libraryIdentifier.getId(), libraryIdentifier.getVersion()));

    }

    @Override
    public boolean isLibrarySourceAvailable(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        validateInput(libraryIdentifier, type);

        InputStream source;
        for (LibrarySourceProvider provider : getProviders()) {
            if (provider instanceof LibrarySourceProviderExt) {
                LibrarySourceProviderExt providerExt = (LibrarySourceProviderExt) provider;
                if (providerExt.isLibrarySourceAvailable(libraryIdentifier, type)) {
                    return true;
                }
            } else {
                source = provider.getLibrarySource(libraryIdentifier);
                if (source != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        return getLibrarySource(libraryIdentifier, LibraryContentType.CQL);
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
