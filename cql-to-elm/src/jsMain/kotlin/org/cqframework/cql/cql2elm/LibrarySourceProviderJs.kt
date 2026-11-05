package org.cqframework.cql.cql2elm

actual fun getLibrarySourceProviders(refresh: Boolean): Iterator<LibrarySourceProvider> {
    // No-op implementation for platforms without ServiceLoader support
    return emptyList<LibrarySourceProvider>().iterator()
}
