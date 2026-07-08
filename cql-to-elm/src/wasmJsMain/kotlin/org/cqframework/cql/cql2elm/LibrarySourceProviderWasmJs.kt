@file:OptIn(ExperimentalJsExport::class, ExperimentalWasmJsInterop::class)

package org.cqframework.cql.cql2elm

import kotlinx.io.Source

actual fun getLibrarySourceProviders(refresh: Boolean): Iterator<LibrarySourceProvider> {
    // No-op implementation for platforms without ServiceLoader support
    return emptyList<LibrarySourceProvider>().iterator()
}

@JsExport
@JsName("createLibrarySourceProvider")
fun createLibrarySourceProviderReference(
    getLibraryCql: (id: String, system: String?, version: String?) -> JsReference<Source>?
): JsReference<LibrarySourceProvider> {
    return createLibrarySourceProvider { id, system, version ->
            val cql = getLibraryCql(id, system, version)
            cql?.get()
        }
        .toJsReference()
}
