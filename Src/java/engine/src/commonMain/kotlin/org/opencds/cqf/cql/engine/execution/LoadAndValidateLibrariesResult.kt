package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.cql2elm.CqlCompilerException
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.exception.CqlException

/**
 * Track results and exceptions for multiple libraries in a single load operation, to support
 * partial successes and partial failures across libraries.
 */
class LoadAndValidateLibrariesResult(builder: Builder) {
    private val results = builder.results.toMap()
    val exceptions = builder.exceptions.toMap()
    val warnings = builder.warnings.toMap()

    fun libraryCount(): Int {
        return results.size
    }

    val allLibraryIds: List<VersionedIdentifier>
        get() = results.keys.toList()

    val allLibraries: List<Library>
        get() = results.values.toList()

    fun getLibraryIdentifierAtIndex(index: Int): VersionedIdentifier? {
        check(!(index < 0 || index >= this.allLibraries.size)) { "Index out of bounds: $index" }
        return this.allLibraryIds[index]
    }

    fun retrieveLibrary(libraryIdentifier: VersionedIdentifier): Library? {
        if (libraryIdentifier.version != null) {
            require(results.containsKey(libraryIdentifier)) {
                "libraryIdentifier '${libraryIdentifier}' does not exist."
            }

            return results[libraryIdentifier]
        }

        return results.entries
            .filter { entry -> entry.key.id.equals(libraryIdentifier.id) }
            .map { entry -> entry.value }
            .firstOrNull()
            ?: throw IllegalArgumentException("library id ${libraryIdentifier.id} not found.")
    }

    class Builder {
        val results = LinkedHashMap<VersionedIdentifier, Library>()
        val exceptions = LinkedHashMap<VersionedIdentifier, RuntimeException>()
        val warnings = LinkedHashMap<VersionedIdentifier, RuntimeException>()

        fun addResult(libraryId: VersionedIdentifier, library: Library) {
            this.results[libraryId] = library
        }

        fun addExceptionOrWarning(libraryId: VersionedIdentifier, exception: RuntimeException) {
            addExceptionsOrWarnings(libraryId, listOf(exception))
        }

        fun addExceptionsOrWarnings(
            libraryId: VersionedIdentifier,
            exceptions: List<RuntimeException>?,
        ) {
            if (exceptions == null || exceptions.isEmpty()) {
                return
            }

            exceptions
                .filter { obj -> obj !is CqlCompilerException }
                .forEach { nonCompilerException ->
                    this.exceptions[libraryId] = nonCompilerException
                }

            val exceptionsBySeverity =
                exceptions.filterIsInstance<CqlCompilerException>().groupBy { obj -> obj.severity }

            for (exceptionsGroupedBySeverity in exceptionsBySeverity.entries) {
                val wrappedExceptions: CqlException =
                    wrapExceptions(libraryId, exceptionsGroupedBySeverity.value)

                if (CqlCompilerException.ErrorSeverity.Error == exceptionsGroupedBySeverity.key) {
                    this.exceptions[libraryId] = wrappedExceptions
                } else {
                    this.warnings[libraryId] = wrappedExceptions
                }
            }
        }

        fun build(): LoadAndValidateLibrariesResult {
            return LoadAndValidateLibrariesResult(this)
        }

        companion object {
            private fun wrapExceptions(
                libraryIdentifier: VersionedIdentifier,
                exceptions: List<CqlCompilerException>,
            ): CqlException {
                return CqlException(
                    "Library ${libraryIdentifier.id
                            + (if (libraryIdentifier.version != null)
                        "-" + libraryIdentifier.version
                    else
                        "")} loaded, but had errors: ${exceptions.joinToString(", ") { obj -> obj.message ?: "" }}"
                )
            }
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
