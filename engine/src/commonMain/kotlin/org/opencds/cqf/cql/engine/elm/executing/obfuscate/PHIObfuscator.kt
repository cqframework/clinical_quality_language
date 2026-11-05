package org.opencds.cqf.cql.engine.elm.executing.obfuscate

import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.runtime.Value

@JsOnlyExport
interface PHIObfuscator {
    fun obfuscate(source: Value?): String?
}
