@file:OptIn(ExperimentalJsExport::class, ExperimentalWasmJsInterop::class)

package org.cqframework.cql.cql2elm.utils

import kotlinx.io.Source

@JsExport
fun stringAsSource(string: String): JsReference<Source> {
    return string.asSource().toJsReference()
}
