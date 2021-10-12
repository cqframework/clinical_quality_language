package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Used by LibraryManager to manage a set of library source providers that
 * resolve library includes within CQL. Package private since its not intended
 * to be used outside the context of the instantiating LibraryManager instance.
 */
public class PriorityLibrarySourceLoader implements LibrarySourceLoaderExt, NamespaceAware {
    private final List<LibrarySourceProvider> PROVIDERS = new ArrayList<>();
    private Set<LibraryContentType> supportedTypes = new HashSet<>();

    @Override
    public void registerProvider(LibrarySourceProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider is null.");
        }

        if(supportedTypes == null) {
            supportedTypes = new HashSet<>();
        }

        if(provider instanceof LibrarySourceProviderExt) {
            LibrarySourceProviderExt providerExt = (LibrarySourceProviderExt) provider;
            supportedTypes.add(providerExt.getLibrarySourceType());
        }

        if (provider instanceof NamespaceAware) {
            ((NamespaceAware)provider).setNamespaceManager(namespaceManager);
        }

        PROVIDERS.add(provider);
    }

    @Override
    public void clearProviders() {
        PROVIDERS.clear();
    }

    @Override
    public Set<LibraryContentType> getSupportedContentTypes() {
        return supportedTypes;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier, LibraryContentType type) {

        validateInput(libraryIdentifier, type);
        InputStream source = null;
        for (LibrarySourceProvider provider : PROVIDERS) {
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
        for (LibrarySourceProvider provider : PROVIDERS) {
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

        for (LibrarySourceProvider provider : PROVIDERS) {
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
