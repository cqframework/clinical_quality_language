@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmOverloads
import kotlinx.io.Source
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.ucum.UcumService
import org.cqframework.cql.cql2elm.ucum.defaultLazyUcumService
import org.cqframework.cql.cql2elm.utils.logger
import org.cqframework.cql.elm.serializing.DefaultElmLibraryReaderProvider
import org.cqframework.cql.elm.serializing.ElmLibraryReaderProvider
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Manages a set of CQL libraries. As new library references are encountered during compilation, the
 * corresponding source is obtained via librarySourceLoader, compiled and cached for later use.
 */
@JsOnlyExport
@Suppress("TooManyFunctions", "LongParameterList", "NON_EXPORTABLE_TYPE")
class LibraryManager
@JvmOverloads
constructor(
    val modelManager: ModelManager,
    val cqlCompilerOptions: CqlCompilerOptions = CqlCompilerOptions.defaultOptions(),
    libraryCache: MutableMap<VersionedIdentifier, CompiledLibrary>? = null,
    lazyUcumService: Lazy<UcumService> = defaultLazyUcumService,
    val elmLibraryReaderProvider: ElmLibraryReaderProvider = DefaultElmLibraryReaderProvider,
) {
    enum class CacheMode {
        NONE,
        READ_ONLY,
        READ_WRITE,
    }

    val namespaceManager: NamespaceManager = modelManager.namespaceManager
    val compiledLibraries = libraryCache ?: HashMap()
    val librarySourceLoader: LibrarySourceLoader =
        DefaultLibrarySourceLoader(modelManager.namespaceManager).also {
            if (modelManager.path != null) {
                it.setPath(modelManager.path)
            }
        }

    val ucumService by lazyUcumService

    /**
     * A "well-known" library name is one that is allowed to resolve without a namespace in a
     * namespace-aware context
     */
    fun isWellKnownLibraryName(unqualifiedIdentifier: String?): Boolean {
        if (unqualifiedIdentifier == null) {
            return false
        }

        return unqualifiedIdentifier == "FHIRHelpers"
    }

    @JsExport.Ignore
    fun resolveLibrary(
        libraryIdentifier: VersionedIdentifier,
        cacheMode: CacheMode,
    ): CompiledLibrary {
        return this.resolveLibraryInner(libraryIdentifier, cacheMode).compiledLibrary
    }

    @JsExport.Ignore
    fun resolveLibrary(libraryIdentifier: VersionedIdentifier): CompiledLibrary {
        return this.resolveLibraryInner(libraryIdentifier, CacheMode.READ_WRITE).compiledLibrary
    }

    fun canResolveLibrary(libraryIdentifier: VersionedIdentifier): Boolean {
        // This throws an exception if the library cannot be resolved
        this.resolveLibrary(libraryIdentifier)
        return true
    }

    @JsExport.Ignore
    fun resolveLibrary(
        libraryIdentifier: VersionedIdentifier,
        errors: MutableList<CqlCompilerException>,
    ): CompiledLibrary {
        val compiledLibraryResult =
            this.resolveLibraryInner(libraryIdentifier, CacheMode.READ_WRITE)
        errors.addAll(compiledLibraryResult.errors)
        return compiledLibraryResult.compiledLibrary
    }

    private fun resolveLibraryInner(
        libraryIdentifier: VersionedIdentifier,
        cacheMode: CacheMode,
    ): CompiledLibraryResult {
        require(!libraryIdentifier.id.isNullOrEmpty()) { "libraryIdentifier Id is null." }

        val compiledLibraryResults = resolveLibraries(mutableListOf(libraryIdentifier), cacheMode)

        return compiledLibraryResults.onlyResult
    }

    fun resolveLibraries(
        libraryIdentifiers: List<VersionedIdentifier>
    ): CompiledLibraryMultiResults {
        return resolveLibraries(libraryIdentifiers, CacheMode.READ_WRITE)
    }

    private fun resolveLibraries(
        libraryIdentifiers: List<VersionedIdentifier>,
        cacheMode: CacheMode,
    ): CompiledLibraryMultiResults {
        require(libraryIdentifiers.isNotEmpty()) { "libraryIdentifier can not be null" }

        require(
            libraryIdentifiers.all { libraryIdentifier -> !libraryIdentifier.id.isNullOrEmpty() }
        ) {
            "at least one libraryIdentifier Id is null"
        }

        val compiledLibraryResults = ArrayList<CompiledLibraryResult>()

        if (cacheMode != CacheMode.NONE) {
            // Ensure that cache retrieved librariesFromCache are in the same order as the input
            // identifiers so we don't get a mismatch later

            val librariesFromCache =
                libraryIdentifiers
                    .filter { it in compiledLibraries }
                    .associateWith { compiledLibraries[it]!! }

            if (librariesFromCache.size == libraryIdentifiers.size) {
                return CompiledLibraryMultiResults.from(
                    librariesFromCache.map { (identifier, lib) ->
                        CompiledLibraryResult(identifier, lib, mutableListOf())
                    }
                )
            }

            librariesFromCache
                .map { (identifier, libraryFromCache) ->
                    CompiledLibraryResult(identifier, libraryFromCache, mutableListOf())
                }
                .forEach { e -> compiledLibraryResults.add(e) }
        }

        for (libraryIdentifier in libraryIdentifiers) {
            if (isLibraryAlreadyRetrievedFromCache(libraryIdentifier, compiledLibraryResults)) {
                logger.debug {
                    "library ${libraryIdentifier.id} already in cache, skipping compilation"
                }
                continue
            }

            val compiledLibraryResult = compileLibrary(libraryIdentifier)

            // If we have any errors, ignore the compiled library altogether just like in the single
            // lib case
            if (
                !CqlCompilerException.hasErrors(compiledLibraryResult.errors) &&
                    cacheMode == CacheMode.READ_WRITE
            ) {
                logger.debug { "adding library to cache: ${libraryIdentifier.id}" }
                compiledLibraries[libraryIdentifier] = compiledLibraryResult.compiledLibrary
            }

            compiledLibraryResults.add(compiledLibraryResult)
        }

        return CompiledLibraryMultiResults.from(compiledLibraryResults)
    }

    private fun isLibraryAlreadyRetrievedFromCache(
        searchedForLibraryIdentifier: VersionedIdentifier,
        compiledLibrariesFromCache: MutableList<CompiledLibraryResult>,
    ): Boolean {
        return compiledLibrariesFromCache
            .map { it.compiledLibrary.identifier }
            .map { compiledLibraryIdentifier ->
                stripVersionIfNeeded(compiledLibraryIdentifier!!, searchedForLibraryIdentifier)
            }
            .toList()
            .contains(searchedForLibraryIdentifier)
    }

    private fun stripVersionIfNeeded(
        compiledLibraryIdentifier: VersionedIdentifier,
        searchedForLibraryIdentifier: VersionedIdentifier,
    ): VersionedIdentifier {
        if (searchedForLibraryIdentifier.version == null) {
            return VersionedIdentifier().withId(compiledLibraryIdentifier.id)
        }
        return compiledLibraryIdentifier
    }

    @Suppress("ThrowsCount")
    private fun compileLibrary(libraryIdentifier: VersionedIdentifier): CompiledLibraryResult {
        val libraryPath = NamespaceManager.getPath(libraryIdentifier.system, libraryIdentifier.id!!)

        if (!this.cqlCompilerOptions.enableCqlOnly) {
            val elmCompiledLibrary = tryCompiledLibraryElm(libraryIdentifier, cqlCompilerOptions)
            if (elmCompiledLibrary != null) {
                validateIdentifiers(libraryIdentifier, elmCompiledLibrary, libraryPath)
                sortStatements(elmCompiledLibrary)
                return CompiledLibraryResult(libraryIdentifier, elmCompiledLibrary, mutableListOf())
            }
        }

        val cqlSource =
            librarySourceLoader.getLibrarySource(libraryIdentifier)
                ?: throw CqlIncludeException(
                    @Suppress("MaxLineLength")
                    "Could not load source for library ${libraryIdentifier.id}, version ${libraryIdentifier.version}, namespace uri ${libraryIdentifier.system}.",
                    libraryIdentifier.system,
                    libraryIdentifier.id!!,
                    libraryIdentifier.version,
                )

        val compiler =
            CqlCompiler(
                libraryIdentifier.system?.let { namespaceManager.getNamespaceInfoFromUri(it) },
                libraryIdentifier,
                this,
            )

        cqlSource.use { compiler.run(it) }

        val compiledLibrary =
            compiler.compiledLibrary
                ?: throw CqlIncludeException(
                    "Could not load source for library $libraryPath, version ${libraryIdentifier.version}.",
                    libraryIdentifier.system,
                    libraryIdentifier.id!!,
                    libraryIdentifier.version,
                )

        validateIdentifiers(libraryIdentifier, compiledLibrary, libraryPath)

        sortStatements(compiledLibrary)
        return CompiledLibraryResult(libraryIdentifier, compiledLibrary, compiler.exceptions)
    }

    private fun validateIdentifiers(
        libraryIdentifier: VersionedIdentifier,
        result: CompiledLibrary,
        libraryPath: String,
    ) {

        val resultIdentifier = result.identifier!!

        val areIdsEqual = libraryIdentifier.id.equals(resultIdentifier.id)
        val libraryIdentifierVersion = libraryIdentifier.version
        val resultIdentifierVersion = resultIdentifier.version

        // If the library VersionedIdentifier used to query is null, then don't compare to the
        // result library version, since we're doing a broader search
        val areIdentifiersValid: Boolean
        if (libraryIdentifierVersion == null) {
            areIdentifiersValid = areIdsEqual
        } else {
            val areVersionsEqual = libraryIdentifierVersion == resultIdentifier.version
            areIdentifiersValid = areIdsEqual && areVersionsEqual
        }

        if (!areIdentifiersValid) {
            throw CqlIncludeException(
                @Suppress("MaxLineLength")
                "Library $libraryPath was included with version $libraryIdentifierVersion, but id: ${resultIdentifier.id} and version $resultIdentifierVersion of the library was found.",
                libraryIdentifier.system,
                libraryIdentifier.id!!,
                libraryIdentifierVersion ?: "null",
            )
        }
    }

    private fun sortStatements(compiledLibrary: CompiledLibrary) {
        if (compiledLibrary.library!!.statements == null) {
            return
        }
        compiledLibrary.library!!.statements!!.def.sortBy { it.name }
    }

    private fun tryCompiledLibraryElm(
        libraryIdentifier: VersionedIdentifier,
        options: CqlCompilerOptions,
    ): CompiledLibrary? {
        var elm: Source?
        @Suppress("LoopWithTooManyJumpStatements")
        for (type: LibraryContentType in supportedContentTypes) {
            if (LibraryContentType.CQL == type) {
                continue
            }
            elm = librarySourceLoader.getLibraryContent(libraryIdentifier, type)
            if (elm == null) {
                continue
            }
            return generateCompiledLibraryFromElm(libraryIdentifier, elm, type, options)
        }
        return null
    }

    private fun generateCompiledLibraryFromElm(
        @Suppress("UnusedParameter") libraryIdentifier: VersionedIdentifier,
        librarySource: Source,
        type: LibraryContentType,
        @Suppress("UnusedParameter") options: CqlCompilerOptions,
    ): CompiledLibrary? {
        val library =
            try {
                this.elmLibraryReaderProvider.create(type.mimeType()).read(librarySource)
            } catch (@Suppress("SwallowedException", "TooGenericExceptionCaught") e: Exception) {
                // intentionally ignored
                return null
            }
        var compiledLibrary: CompiledLibrary? = null
        if (checkBinaryCompatibility(library)) {
            compiledLibrary = generateCompiledLibrary(library)
        }
        return compiledLibrary
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth", "ReturnCount")
    private fun generateCompiledLibrary(library: Library): CompiledLibrary? {
        try {

            val compiledLibrary = CompiledLibrary()
            compiledLibrary.library = library
            compiledLibrary.identifier = library.identifier
            library.usings?.def?.forEach { compiledLibrary.add(it) }

            library.includes?.def?.forEach { compiledLibrary.add(it) }

            library.codeSystems?.def?.forEach { compiledLibrary.add(it) }

            library.valueSets?.def?.forEach { compiledLibrary.add(it) }

            library.codes?.def?.forEach { compiledLibrary.add(it) }

            library.concepts?.def?.forEach { compiledLibrary.add(it) }

            library.parameters?.def?.forEach { compiledLibrary.add(it) }

            library.statements?.def?.forEach {
                requireNotNull(it.resultType) {
                    "Expression ${it.name} in library ${library.identifier?.id} does not have a result type."
                }

                compiledLibrary.add(it)
            }

            return compiledLibrary
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "Error generating compiled library" }
            return null
        }
    }

    private fun compilerOptionsMatch(library: Library): Boolean {
        val compilerOptions: Set<CqlCompilerOptions.Options> =
            CompilerOptions.getCompilerOptions(library) ?: return false
        return (compilerOptions == cqlCompilerOptions.options)
    }

    private fun checkBinaryCompatibility(library: Library?): Boolean {
        if (library == null) {
            return false
        }
        return (isSignatureCompatible(library) &&
            isVersionCompatible(library) &&
            compilerOptionsMatch(library))
    }

    private fun isSignatureCompatible(library: Library): Boolean {
        return !hasOverloadedFunctions(library) || hasSignature(library)
    }

    @Suppress("ReturnCount")
    private fun hasOverloadedFunctions(library: Library): Boolean {
        if (library.statements == null) {
            return false
        }
        val functionNames: MutableSet<FunctionSig> = HashSet()
        for (ed in library.statements!!.def) {
            if (ed is FunctionDef) {
                val fd: FunctionDef = ed
                val sig = FunctionSig(fd.name!!, fd.operand.size)
                if (functionNames.contains(sig)) {
                    return true
                } else {
                    functionNames.add(sig)
                }
            }
        }
        return false
    }

    @Suppress("NestedBlockDepth")
    private fun hasSignature(library: Library): Boolean {
        if (library.statements != null) {
            // Just a quick top-level scan for signatures. To fully verify we'd have to recurse all
            // the way down. At that point, let's just translate.
            for (ed in library.statements!!.def) {
                if (ed.expression is FunctionRef) {
                    val fr: FunctionRef = ed.expression as FunctionRef
                    if (fr.signature.isNotEmpty()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun isVersionCompatible(library: Library): Boolean {
        if (this.cqlCompilerOptions.compatibilityLevel.isNotEmpty()) {
            val version = CompilerOptions.getCompilerVersion(library)
            if (version != null) {
                return version == this.cqlCompilerOptions.compatibilityLevel
            }
        }
        return false
    }

    internal class FunctionSig(private val name: String, private val numArguments: Int) {
        override fun hashCode(): Int {
            val prime = 31
            var result = 1
            result = prime * result + name.hashCode()
            result = prime * result + numArguments
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null) return false
            if (other is FunctionSig) {
                return (other.name == name) && other.numArguments == numArguments
            }
            return false
        }
    }

    companion object {
        private val supportedContentTypes: Array<LibraryContentType> =
            arrayOf(LibraryContentType.JSON, LibraryContentType.XML, LibraryContentType.CQL)
    }
}
