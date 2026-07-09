@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm.utils

import kotlinx.io.Source

@JsExport
@Suppress("NON_EXPORTABLE_TYPE")
fun stringAsSource(string: String): Source {
    return string.asSource()
}
