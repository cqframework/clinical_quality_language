package org.cqframework.cql.cql2elm

import java.io.InputStream
import java.nio.file.Path
import org.hl7.cql.model.NamespaceAware
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Used by LibraryManager to manage a set of library source providers that resolve library includes
 * within CQL. Package private since its not intended to be used outside the context of the
 * instantiating LibraryManager instance.
 */
internal class DefaultLibrarySourceLoader : LibrarySourceLoader, NamespaceAware, PathAware {
    private val providers: MutableList<LibrarySourceProvider> = ArrayList()
    var initialized: Boolean = false

    override fun registerProvider(provider: LibrarySourceProvider?) {
        require(provider != null) { "provider is null." }
        if (provider is NamespaceAware) {
            (provider as NamespaceAware).setNamespaceManager(namespaceManager)
        }
        if (provider is PathAware) {
            (provider as PathAware).setPath(path)
        }
        providers.add(provider)
    }

    private var path: Path? = null

    override fun setPath(path: Path?) {
        if (path == null || !path.toFile().isDirectory) {
            throw IllegalArgumentException(
                @Suppress("ImplicitDefaultLocale")
                String.format("path '%s' is not a valid directory", path)
            )
        }
        this.path = path
        for (provider: LibrarySourceProvider? in getProviders()) {
            if (provider is PathAware) {
                (provider as PathAware).setPath(path)
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

    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier?): InputStream {
        require(libraryIdentifier != null) { "libraryIdentifier is null." }
        require(!libraryIdentifier.id.isNullOrEmpty()) { "libraryIdentifier Id is null." }
        var source: InputStream? = null
        for (provider: LibrarySourceProvider in getProviders()) {
            val localSource: InputStream? = provider.getLibrarySource(libraryIdentifier)
            if (localSource != null) {
                require(source == null) {
                    @Suppress("ImplicitDefaultLocale")
                    String.format(
                        "Multiple sources found for library %s, version %s.",
                        libraryIdentifier.id,
                        libraryIdentifier.version
                    )
                }
                source = localSource
            }
        }
        if (source == null) {
            throw IllegalArgumentException(
                @Suppress("ImplicitDefaultLocale")
                String.format(
                    "Could not load source for library %s, version %s.",
                    libraryIdentifier.id,
                    libraryIdentifier.version
                )
            )
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
