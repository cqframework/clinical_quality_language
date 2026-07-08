package org.cqframework.fhir.utilities.exception

/**
 * This exception is thrown whenever there is an issue initializing the tooling from a source IG.
 */
class IGInitializationException : RuntimeException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    companion object {
        const val serialVersionUID: Long = 1L
    }
}
