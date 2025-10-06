package org.cqframework.fhir.npm

/** This exception is thrown whenever there is an issue with the NpmPackageManager. */
class NpmPackageManagerException : RuntimeException {
    constructor(message: String?, t: Throwable? = null) : super(message, t)
}
