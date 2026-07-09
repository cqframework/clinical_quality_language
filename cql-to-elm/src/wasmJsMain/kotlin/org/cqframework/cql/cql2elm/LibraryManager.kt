@file:OptIn(ExperimentalJsExport::class, ExperimentalWasmJsInterop::class)

package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.ucum.UcumService

@JsExport
fun createLibraryManager(
    modelManager: JsReference<ModelManager>,
    ucumService: JsReference<Lazy<UcumService>>,
): JsReference<LibraryManager> {
    return LibraryManager(modelManager.get(), lazyUcumService = ucumService.get()).toJsReference()
}

@JsExport
fun libraryManagerClearLibrarySourceProviders(libraryManager: JsReference<LibraryManager>) {
    libraryManager.get().librarySourceLoader.clearProviders()
}

@JsExport
fun libraryManagerRegisterLibrarySourceProvider(
    libraryManager: JsReference<LibraryManager>,
    librarySourceProvider: JsReference<LibrarySourceProvider>,
) {
    libraryManager.get().librarySourceLoader.registerProvider(librarySourceProvider.get())
}

@JsExport
fun libraryManagerAddCompilerOption(libraryManager: JsReference<LibraryManager>, option: String) {

    libraryManager.get().cqlCompilerOptions.options.add(CqlCompilerOptions.Options.valueOf(option))
}

@JsExport
fun libraryManagerRemoveCompilerOption(
    libraryManager: JsReference<LibraryManager>,
    option: String,
) {
    libraryManager
        .get()
        .cqlCompilerOptions
        .options
        .remove(CqlCompilerOptions.Options.valueOf(option))
}

@JsExport
fun libraryManagerSetSignatureLevel(
    libraryManager: JsReference<LibraryManager>,
    signatureLevel: String,
) {
    libraryManager.get().cqlCompilerOptions.signatureLevel =
        LibraryBuilder.SignatureLevel.valueOf(signatureLevel)
}
