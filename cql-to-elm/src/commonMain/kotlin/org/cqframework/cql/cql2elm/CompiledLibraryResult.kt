package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Represents the result of compiling a CQL library, including the compiled library and any
 * compilation errors, in order to support a partially successful compile with errors.
 *
 * @param identifier The identifier used to load the library. Note that this identifier may not be
 *   strictly equal to `compiledLibrary.identifier` (same as `compiledLibrary.library.identifier`)
 *   when e.g. the versioned library was resolved only by name.
 * @param compiledLibrary The compiled library.
 * @param errors Compilation exceptions.
 */
data class CompiledLibraryResult(
    val identifier: VersionedIdentifier,
    val compiledLibrary: CompiledLibrary,
    val errors: List<CqlCompilerException>,
)
