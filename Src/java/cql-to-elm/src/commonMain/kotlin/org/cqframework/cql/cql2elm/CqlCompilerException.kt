package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.tracking.TrackBack
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.jvm.Transient

open class CqlCompilerException
@JvmOverloads
constructor(
    message: String?,
    val severity: ErrorSeverity = ErrorSeverity.Error,
    @field:Transient
    val locator: TrackBack? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    enum class ErrorSeverity {
        Info,
        Warning,
        Error
    }

    constructor(
        message: String?,
        cause: Throwable?
    ) : this(message, ErrorSeverity.Error, null, cause)

    constructor(
        message: String?,
        severity: ErrorSeverity,
        cause: Throwable?
    ) : this(message, severity, null, cause)

    constructor(
        message: String?,
        locator: TrackBack?
    ) : this(message, ErrorSeverity.Error, locator, null)

    constructor(
        message: String?,
        locator: TrackBack?,
        cause: Throwable?
    ) : this(message, ErrorSeverity.Error, locator, cause)

    companion object {
        @JvmStatic
        fun hasErrors(exceptions: List<CqlCompilerException>): Boolean {
            for (exception in exceptions) {
                if (exception.severity == ErrorSeverity.Error) {
                    return true
                }
            }
            return false
        }
    }
}
