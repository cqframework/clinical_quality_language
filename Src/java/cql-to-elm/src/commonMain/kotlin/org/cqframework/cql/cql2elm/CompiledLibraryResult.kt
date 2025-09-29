package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary

/**
 * Represents the result of compiling a CQL library, including the compiled library and any
 * compilation errors, in order to support a partially successful compile with errors
 */
class CompiledLibraryResult(
    val compiledLibrary: CompiledLibrary,
    val errors: List<CqlCompilerException>,
) {
    override fun toString(): String {
        return "${CompiledLibraryResult::class.simpleName}[compiledLibrary=$compiledLibrary, errors=$errors]"
    }
}
