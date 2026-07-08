@file:OptIn(ExperimentalJsExport::class, ExperimentalWasmJsInterop::class)

package org.cqframework.cql.cql2elm

import org.hl7.cql.model.ModelInfoProvider

@JsExport
fun createModelManager(): JsReference<ModelManager> {
    return ModelManager().toJsReference()
}

@JsExport
fun modelManagerClearModelInfoProviders(modelManager: JsReference<ModelManager>) {
    modelManager.get().modelInfoLoader.clearModelInfoProviders()
}

@JsExport
fun modelManagerRegisterModelInfoProvider(
    modelManager: JsReference<ModelManager>,
    modelInfoProvider: JsReference<ModelInfoProvider>,
) {
    modelManager.get().modelInfoLoader.registerModelInfoProvider(modelInfoProvider.get())
}
