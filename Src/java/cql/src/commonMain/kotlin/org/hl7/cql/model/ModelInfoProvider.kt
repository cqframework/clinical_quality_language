package org.hl7.cql.model

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.elm_modelinfo.r1.ModelInfo

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
interface ModelInfoProvider {
    fun load(modelIdentifier: ModelIdentifier): ModelInfo?
}
