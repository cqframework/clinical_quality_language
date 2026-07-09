@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm.utils

import kotlinx.io.files.Path

@JsExport
@Suppress("NON_EXPORTABLE_TYPE")
fun stringAsPath(string: String): Path {
    return Path(string)
}
