package org.cqframework.cql.cql2elm

import org.hl7.cql_annotations.r1.ErrorSeverity
import org.hl7.cql_annotations.r1.ErrorType
import org.hl7.cql_annotations.r1.ObjectFactory
import org.hl7.elm.r1.Library

/**
 * Routes compilation diagnostics to the configured severity-qualified lists and mirrors reportable
 * errors onto the output [Library]'s annotation list so downstream consumers can surface them
 * directly.
 */
internal class ErrorReporter(
    private val options: CqlCompilerOptions,
    private val exceptions: MutableList<CqlCompilerException>,
    private val errors: MutableList<CqlCompilerException>,
    private val warnings: MutableList<CqlCompilerException>,
    private val messages: MutableList<CqlCompilerException>,
    private val libraryProvider: () -> Library,
) {
    private val af = ObjectFactory()

    /**
     * Record a parsing/compilation exception. It is always added to [exceptions] and the
     * severity-specific list; if its severity meets the configured error level it is also added as
     * an annotation to the output library.
     */
    fun recordParsingException(e: CqlCompilerException) {
        addException(e)
        if (shouldReport(e.severity)) {
            val err = af.createCqlToElmError()
            err.message = e.message
            err.errorSeverity = toErrorSeverity(e.severity)
            e.locator?.let { loc ->
                loc.library?.let { lib ->
                    err.librarySystem = lib.system
                    err.libraryId = lib.id
                    err.libraryVersion = lib.version
                }
                err.startLine = loc.startLine
                err.endLine = loc.endLine
                err.startChar = loc.startChar
                err.endChar = loc.endChar
            }
            if (e is CqlIncludeException) {
                err.targetIncludeLibrarySystem = e.librarySystem
                err.targetIncludeLibraryId = e.libraryId
                err.targetIncludeLibraryVersionId = e.versionId
            }

            err.errorType =
                when (e) {
                    is CqlSyntaxException -> ErrorType.SYNTAX
                    is CqlIncludeException -> ErrorType.INCLUDE
                    is CqlSemanticException -> ErrorType.SEMANTIC
                    else -> ErrorType.INTERNAL
                }

            libraryProvider().annotation.add(err)
        }
    }

    private fun addException(e: CqlCompilerException) {
        exceptions.add(e)
        when (e.severity) {
            CqlCompilerException.ErrorSeverity.Error -> errors.add(e)
            CqlCompilerException.ErrorSeverity.Warning -> warnings.add(e)
            CqlCompilerException.ErrorSeverity.Info -> messages.add(e)
        }
    }

    private fun shouldReport(errorSeverity: CqlCompilerException.ErrorSeverity): Boolean =
        when (options.errorLevel) {
            CqlCompilerException.ErrorSeverity.Info -> true
            CqlCompilerException.ErrorSeverity.Warning ->
                errorSeverity == CqlCompilerException.ErrorSeverity.Warning ||
                    errorSeverity == CqlCompilerException.ErrorSeverity.Error
            CqlCompilerException.ErrorSeverity.Error ->
                errorSeverity == CqlCompilerException.ErrorSeverity.Error
            else -> throw IllegalArgumentException("Unknown error severity $errorSeverity")
        }

    private fun toErrorSeverity(severity: CqlCompilerException.ErrorSeverity): ErrorSeverity =
        when (severity) {
            CqlCompilerException.ErrorSeverity.Info -> ErrorSeverity.INFO
            CqlCompilerException.ErrorSeverity.Warning -> ErrorSeverity.WARNING
            CqlCompilerException.ErrorSeverity.Error -> ErrorSeverity.ERROR
        }
}
