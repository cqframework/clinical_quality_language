package org.opencds.cqf.cql.engine.elm.executing.obfuscate

class RedactingPHIObfuscator : PHIObfuscator {
    override fun obfuscate(source: Any?): String {
        return REDACTED_MESSAGE
    }

    companion object {
        const val REDACTED_MESSAGE: String = "<redacted>"
    }
}
