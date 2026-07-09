package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.jvm.JvmStatic
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
abstract class CqlCompilerException(
    message: String,
    var locator: TrackBack? = null,
    val severity: ErrorSeverity = ErrorSeverity.Error,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    enum class ErrorSeverity {
        Info,
        Warning,
        Error,
    }

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
