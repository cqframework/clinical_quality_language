package org.cqframework.cql.cql2elm

/** @author mhadley */
interface LibrarySourceLoader : CommonLibrarySourceLoader {
    fun clearProviders()

    fun registerProvider(provider: LibrarySourceProvider)
}
