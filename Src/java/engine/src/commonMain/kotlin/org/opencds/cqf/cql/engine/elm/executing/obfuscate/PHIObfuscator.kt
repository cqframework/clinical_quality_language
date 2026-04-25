package org.opencds.cqf.cql.engine.elm.executing.obfuscate

import org.opencds.cqf.cql.engine.runtime.CqlType

interface PHIObfuscator {
    fun obfuscate(source: CqlType?): String?
}
