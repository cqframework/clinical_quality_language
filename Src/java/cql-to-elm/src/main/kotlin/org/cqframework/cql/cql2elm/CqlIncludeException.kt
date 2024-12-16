package org.cqframework.cql.cql2elm

class CqlIncludeException : RuntimeException {
    var librarySystem: String?
        private set

    var libraryId: String
        private set

    var versionId: String?
        private set

    constructor(
        message: String?,
        librarySystem: String?,
        libraryId: String,
        versionId: String?
    ) : super(message) {
        this.librarySystem = librarySystem
        this.libraryId = libraryId
        this.versionId = versionId
    }

    constructor(
        message: String?,
        librarySystem: String,
        libraryId: String,
        versionId: String?,
        cause: Throwable?
    ) : super(message, cause) {
        this.librarySystem = librarySystem
        this.libraryId = libraryId
        this.versionId = versionId
    }
}
