package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Represents the result of compiling a CQL library, including the compiled library and any
 * compilation errors, in order to support a partially successful compile with errors
 */
class CompiledLibraryMultiResults(val results: List<CompiledLibraryResult>) {
    fun hasErrors(): Boolean {
        return results.any({ res -> !res.errors.isEmpty() })
    }

    fun allResults(): List<CompiledLibraryResult> {
        return results
    }

    fun getCompiledLibraryFor(libraryIdentifier: VersionedIdentifier): CompiledLibrary? {
        return results
            .firstOrNull { res -> libraryIdentifier.id.equals(res.compiledLibrary.identifier!!.id) }
            ?.compiledLibrary
    }

    fun allCompiledLibraries(): List<CompiledLibrary> {
        return results.map { it.compiledLibrary }
    }

    val onlyResult: CompiledLibraryResult
        get() {
            check(results.size == 1) { "Expected exactly one result, but found " + results.size }
            return results.get(0)
        }

    fun getErrorsFor(libraryIdentifier: VersionedIdentifier): List<CqlCompilerException> {
        return results
            .filter({ res -> libraryIdentifier.equals(res.compiledLibrary.identifier) })
            .flatMap { it.errors }
    }

    fun allLibrariesWithoutErrorSeverity(): List<Library?> {
        return results.filter({ res -> !CqlCompilerException.hasErrors(res.errors) }).map {
            it.compiledLibrary.library
        }
    }

    fun allErrors(): List<CqlCompilerException> {
        return results.flatMap { it.errors }
    }

    companion object {
        fun from(results: List<CompiledLibraryResult>): CompiledLibraryMultiResults {
            return CompiledLibraryMultiResults(results)
        }
    }
}
