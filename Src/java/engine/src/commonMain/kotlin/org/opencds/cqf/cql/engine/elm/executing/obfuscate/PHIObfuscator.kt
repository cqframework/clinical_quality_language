package org.opencds.cqf.cql.engine.elm.executing.obfuscate

import org.opencds.cqf.cql.engine.runtime.Value

interface PHIObfuscator {
    fun obfuscate(source: Value?): String?
}
