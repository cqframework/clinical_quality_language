@file:OptIn(ExperimentalJsExport::class, ExperimentalWasmJsInterop::class)

package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import org.hl7.cql.model.ModelInfoProvider

actual fun getModelInfoProviders(refresh: Boolean): Iterator<ModelInfoProvider> {
    // No-op implementation for platforms without ServiceLoader support
    return emptyList<ModelInfoProvider>().iterator()
}

@JsExport
@JsName("createModelInfoProvider")
fun createModelInfoProviderReference(
    getModelInfoXml: (id: String, system: String?, version: String?) -> JsReference<Source>?
): JsReference<ModelInfoProvider> {
    return createModelInfoProvider { id, system, version ->
            val xml = getModelInfoXml(id, system, version)
            xml?.get()
        }
        .toJsReference()
}
