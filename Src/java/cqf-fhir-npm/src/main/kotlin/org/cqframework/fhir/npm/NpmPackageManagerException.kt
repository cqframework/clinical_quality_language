package org.cqframework.fhir.npm

/** This exception is thrown whenever there is an issue with the NpmPackageManager. */
class NpmPackageManagerException(message: String?, cause: Throwable? = null) :
    RuntimeException(message, cause)
