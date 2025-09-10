package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
open class CqlCompilerException
@JvmOverloads
constructor(
    message: String?,
    val severity: ErrorSeverity = ErrorSeverity.Error,
    val locator: TrackBack? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    enum class ErrorSeverity {
        Info,
        Warning,
        Error
    }

    @JsExport.Ignore
    constructor(
        message: String?,
        cause: Throwable?
    ) : this(message, ErrorSeverity.Error, null, cause)

    @JsExport.Ignore
    constructor(
        message: String?,
        severity: ErrorSeverity,
        cause: Throwable?
    ) : this(message, severity, null, cause)

    @JsExport.Ignore
    constructor(
        message: String?,
        locator: TrackBack?
    ) : this(message, ErrorSeverity.Error, locator, null)

    @JsExport.Ignore
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
