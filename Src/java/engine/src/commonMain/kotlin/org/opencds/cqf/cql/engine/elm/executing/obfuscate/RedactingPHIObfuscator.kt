package org.opencds.cqf.cql.engine.elm.executing.obfuscate

import org.opencds.cqf.cql.engine.runtime.CqlType

class RedactingPHIObfuscator : PHIObfuscator {
    override fun obfuscate(source: CqlType?): String {
        return REDACTED_MESSAGE
    }

    companion object {
        const val REDACTED_MESSAGE: String = "<redacted>"
    }
}
