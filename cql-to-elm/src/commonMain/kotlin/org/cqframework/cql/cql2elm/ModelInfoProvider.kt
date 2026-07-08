package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlinx.io.Source
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

expect fun getModelInfoProviders(refresh: Boolean): Iterator<ModelInfoProvider>

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
fun createModelInfoProvider(
    getModelInfoXml: (id: String, system: String?, version: String?) -> Source?
): ModelInfoProvider {
    return object : ModelInfoProvider {
        override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
            val modelInfoXml =
                getModelInfoXml(modelIdentifier.id, modelIdentifier.system, modelIdentifier.version)
            return modelInfoXml?.let { parseModelInfoXml(it) }
        }
    }
}
