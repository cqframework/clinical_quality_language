package org.cqframework.cql.cql2elm

import java.io.InputStream
import java.nio.file.Path
import kotlin.collections.ArrayList
import org.hl7.cql.model.NamespaceAware
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Used by LibraryManager to manage a set of library source providers that resolve library includes
 * within CQL. Package private since it's not intended to be used outside the context of the
 * instantiating LibraryManager instance.
 */
internal class DefaultLibrarySourceLoader : LibrarySourceLoader, NamespaceAware, PathAware {
    private val providers: MutableList<LibrarySourceProvider> = ArrayList()
    private var initialized: Boolean = false

    override fun registerProvider(provider: LibrarySourceProvider) {
        if (namespaceManager != null && provider is NamespaceAware) {
            provider.setNamespaceManager(namespaceManager!!)
        }
        if (path != null && provider is PathAware) {
            provider.setPath(path!!)
        }
        providers.add(provider)
    }

    private var path: Path? = null

    override fun setPath(path: Path) {
        require(path.toFile().isDirectory) { "path '$path' is not a valid directory" }

        this.path = path
        for (provider in getProviders()) {
            if (provider is PathAware) {
                provider.setPath(path)
            }
        }
    }

    override fun clearProviders() {
        providers.clear()
        initialized = false
    }

    private fun getProviders(): List<LibrarySourceProvider> {
        if (!initialized) {
            initialized = true
            val it: Iterator<LibrarySourceProvider> = LibrarySourceProviderFactory.providers(false)
            while (it.hasNext()) {
                val provider: LibrarySourceProvider = it.next()
                registerProvider(provider)
            }
        }
        return providers
    }

    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): InputStream {
        var source: InputStream? = null
        for (provider: LibrarySourceProvider in getProviders()) {
            val localSource: InputStream? = provider.getLibrarySource(libraryIdentifier)
            if (localSource != null) {
                require(source == null) {
                    "Multiple sources found for library ${libraryIdentifier.id}, version ${libraryIdentifier.version}."
                }
                source = localSource
            }
        }
        requireNotNull(source) {
            "Could not load source for library ${libraryIdentifier.id}, version ${libraryIdentifier.version}."
        }
        return source
    }

    private var namespaceManager: NamespaceManager? = null

    override fun setNamespaceManager(namespaceManager: NamespaceManager) {
        this.namespaceManager = namespaceManager
        for (provider: LibrarySourceProvider? in getProviders()) {
            if (provider is NamespaceAware) {
                (provider as NamespaceAware).setNamespaceManager(namespaceManager)
            }
        }
    }
}
