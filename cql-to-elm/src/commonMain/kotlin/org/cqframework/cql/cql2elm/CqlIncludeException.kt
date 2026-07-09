package org.cqframework.cql.cql2elm

class CqlIncludeException(
    message: String,
    val librarySystem: String? = null,
    val libraryId: String,
    val versionId: String? = null,
    cause: Throwable? = null,
) : CqlCompilerException(message, null, ErrorSeverity.Error, cause)
