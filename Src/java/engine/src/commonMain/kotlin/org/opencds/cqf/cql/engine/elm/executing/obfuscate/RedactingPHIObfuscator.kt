package org.opencds.cqf.cql.engine.elm.executing.obfuscate

import org.opencds.cqf.cql.engine.runtime.Value

class RedactingPHIObfuscator : PHIObfuscator {
    override fun obfuscate(source: Value?): String {
        return REDACTED_MESSAGE
    }

    companion object {
        const val REDACTED_MESSAGE: String = "<redacted>"
    }
}
