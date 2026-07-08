package org.opencds.cqf.cql.engine.elm.executing.obfuscate

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.runtime.Value

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
interface PHIObfuscator {
    fun obfuscate(source: Value?): String?
}
