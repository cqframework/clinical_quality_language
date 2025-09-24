@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm.utils

import kotlinx.io.files.Path

@JsExport
fun stringAsPath(string: String): JsReference<Path> {
    return Path(string).toJsReference()
}
