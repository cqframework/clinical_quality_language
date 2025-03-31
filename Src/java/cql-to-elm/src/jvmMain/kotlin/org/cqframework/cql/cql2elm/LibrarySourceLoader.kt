package org.cqframework.cql.cql2elm

/** @author mhadley */
interface LibrarySourceLoader : ILibrarySourceLoader {
    fun clearProviders()

    fun registerProvider(provider: LibrarySourceProvider)
}
