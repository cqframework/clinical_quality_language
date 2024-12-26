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
class PriorityLibrarySourceLoader : LibrarySourceLoader, NamespaceAware, PathAware {
    private val providers: MutableList<LibrarySourceProvider> = ArrayList()
    private var initialized = false

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
            val it = LibrarySourceProviderFactory.providers(false)
            while (it.hasNext()) {
                val provider = it.next()
                registerProvider(provider)
            }
        }
        return providers
    }

    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): InputStream? {
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL)
    }

    override fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType
    ): InputStream? {
        var content: InputStream?
        for (provider in getProviders()) {
            content = provider.getLibraryContent(libraryIdentifier, type)
            if (content != null) {
                return content
            }
        }
        return null
    }

    private var namespaceManager: NamespaceManager? = null

    override fun setNamespaceManager(namespaceManager: NamespaceManager) {
        this.namespaceManager = namespaceManager
        for (provider in getProviders()) {
            if (provider is NamespaceAware) {
                provider.setNamespaceManager(namespaceManager)
            }
        }
    }
}
