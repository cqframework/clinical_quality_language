package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.LibraryManager
import org.hl7.cql.ast.IncludeDefinition
import org.hl7.cql.ast.Library
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Pre-compiles included libraries via [LibraryManager] so they are cached for on-demand resolution
 * during type inference. Runs after model conversion registration and AgeIn desugaring, before the
 * convergence loop.
 *
 * Two classes of libraries are compiled:
 * 1. **Explicit includes** — declared in CQL source via `include ... called ...`.
 * 2. **Implicit helper libraries** — referenced by model conversion info (e.g., FHIRHelpers for
 *    FHIR models). These are pre-compiled so they are cached in [LibraryManager] for on-demand
 *    resolution during type inference, but they do NOT appear in the AST or ELM output. The legacy
 *    compiler also loads these internally without adding them to the ELM includes.
 *
 * This phase does NOT register included library conversions or operators in the current library's
 * [OperatorRegistry]. Model-declared conversions (e.g., FHIR.dateTime → System.DateTime via
 * FHIRHelpers.ToDateTime) are registered separately by [SemanticAnalyzer] from model info.
 * Cross-library function calls are resolved on-demand by [TypeResolver.resolveLibraryFunctionCall]
 * which calls [LibraryManager.resolveLibrary] (a cache hit after this phase).
 */
class LibraryResolution(
    private val libraryManager: LibraryManager?,
    private val modelContext: ModelContext,
) {

    /** Diagnostics collected during include resolution. */
    val diagnostics = mutableListOf<Diagnostic>()

    /**
     * Pre-compile all included libraries. Safe to call even when no [LibraryManager] is available —
     * does nothing in that case.
     */
    fun resolveIncludes(library: Library) {
        val lm = libraryManager ?: return

        // Pre-compile implicit helper libraries referenced by model conversion info.
        // This ensures they are cached in LibraryManager before type inference starts,
        // so that resolveLibraryFunctionCall can find them on-demand.
        precompileImplicitHelperLibraries(lm, library)

        // Pre-compile explicit includes from CQL source.
        for (includeDef in library.definitions.filterIsInstance<IncludeDefinition>()) {
            precompileExplicitInclude(lm, includeDef)
        }
    }

    /**
     * Pre-compile libraries referenced by model conversion operators but not explicitly included.
     * These are compiled just to cache them — they don't appear in the AST or ELM output.
     */
    private fun precompileImplicitHelperLibraries(lm: LibraryManager, library: Library) {
        val referencedLibNames =
            modelContext.collectModelConversions().mapNotNull { it.operator?.libraryName }.toSet()

        if (referencedLibNames.isEmpty()) return

        val declaredIncludes =
            library.definitions
                .filterIsInstance<IncludeDefinition>()
                .map { it.libraryIdentifier.simpleName }
                .toSet()

        // Infer version from model's using declaration (e.g., FHIR 4.0.1 → FHIRHelpers 4.0.1).
        val helperVersions = ModelIntegration(modelContext).inferHelperLibraryVersions(library)

        for (libName in referencedLibNames) {
            if (libName in declaredIncludes) continue
            val version = helperVersions[libName]
            val libraryId = VersionedIdentifier().withId(libName).withVersion(version)
            try {
                lm.resolveLibrary(libraryId) // compile and cache
            } catch (_: Exception) {
                // Not fatal — model conversions may still work via the ConversionMap.
                // The helper library source may not be available (e.g., test environments
                // without FHIRHelpers on the library source path). No diagnostic for implicit
                // includes since the user didn't explicitly request them.
            }
        }
    }

    private fun precompileExplicitInclude(lm: LibraryManager, includeDef: IncludeDefinition) {
        val libraryId =
            VersionedIdentifier()
                .withId(includeDef.libraryIdentifier.simpleName)
                .withVersion(includeDef.version?.value)
        try {
            lm.resolveLibrary(libraryId)
        } catch (e: Exception) {
            val name = includeDef.libraryIdentifier.simpleName
            val version = includeDef.version?.value
            val versionSuffix = if (version != null) " version '$version'" else ""
            diagnostics.add(
                Diagnostic(
                    Diagnostic.Severity.ERROR,
                    "Could not resolve included library '$name'$versionSuffix: ${e.message}",
                )
            )
        }
    }
}
