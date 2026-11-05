package org.cqframework.cql.cql2elm

import java.util.*

actual fun getLibrarySourceProviders(refresh: Boolean): Iterator<LibrarySourceProvider> {
    val loader: ServiceLoader<LibrarySourceProvider> =
        ServiceLoader.load(LibrarySourceProvider::class.java)
    if (refresh) {
        loader.reload()
    }
    return loader.iterator()
}
