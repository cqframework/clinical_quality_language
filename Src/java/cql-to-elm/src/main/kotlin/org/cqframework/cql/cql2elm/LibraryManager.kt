@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.ucum.UcumService
import org.cqframework.cql.cql2elm.ucum.UcumServiceFactory
import org.cqframework.cql.elm.serializing.ElmLibraryReaderFactory
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.*

/**
 * Manages a set of CQL libraries. As new library references are encountered during compilation, the
 * corresponding source is obtained via librarySourceLoader, compiled and cached for later use.
 */
@Suppress("TooManyFunctions")
class LibraryManager
@JvmOverloads
constructor(
    val modelManager: ModelManager,
    val cqlCompilerOptions: CqlCompilerOptions = CqlCompilerOptions.defaultOptions(),
    libraryCache: MutableMap<VersionedIdentifier, CompiledLibrary>? = null
) {
    enum class CacheMode {
        NONE,
        READ_ONLY,
        READ_WRITE
    }

    var namespaceManager = modelManager.namespaceManager
    var compiledLibraries: MutableMap<VersionedIdentifier, CompiledLibrary> =
        libraryCache ?: HashMap()
    val librarySourceLoader: LibrarySourceLoader = PriorityLibrarySourceLoader()
    val ucumService: UcumService by lazy { UcumServiceFactory.load() }

    /*
     * A "well-known" library name is one that is allowed to resolve without a
     * namespace in a namespace-aware context
     */
    fun isWellKnownLibraryName(unqualifiedIdentifier: String?): Boolean {
        if (unqualifiedIdentifier == null) {
            return false
        }
        return when (unqualifiedIdentifier) {
            "FHIRHelpers" -> true
            else -> false
        }
    }

    fun resolveLibrary(
        libraryIdentifier: VersionedIdentifier?,
        cacheMode: CacheMode
    ): CompiledLibrary {
        return this.resolveLibrary(libraryIdentifier, ArrayList(), cacheMode)
    }

    fun canResolveLibrary(libraryIdentifier: VersionedIdentifier?): Boolean {
        val lib = this.resolveLibrary(libraryIdentifier)
        return lib != null
    }

    @JvmOverloads
    fun resolveLibrary(
        libraryIdentifier: VersionedIdentifier?,
        errors: MutableList<CqlCompilerException> = ArrayList(),
        cacheMode: CacheMode = CacheMode.READ_WRITE
    ): CompiledLibrary {
        require(libraryIdentifier != null) { "libraryIdentifier is null." }
        require(!libraryIdentifier.id.isNullOrEmpty()) { "libraryIdentifier Id is null." }
        var library: CompiledLibrary?
        if (cacheMode != CacheMode.NONE) {
            library = compiledLibraries[libraryIdentifier]
            if (library != null) {
                return library
            }
        }
        library = compileLibrary(libraryIdentifier, errors)
        if (!CqlCompilerException.hasErrors(errors) && cacheMode == CacheMode.READ_WRITE) {
            compiledLibraries[libraryIdentifier] = library
        }
        return library
    }

    @Suppress("LongMethod", "ThrowsCount")
    private fun compileLibrary(
        libraryIdentifier: VersionedIdentifier,
        errors: MutableList<CqlCompilerException>?
    ): CompiledLibrary {
        var result: CompiledLibrary?
        if (!cqlCompilerOptions.enableCqlOnly) {
            result = tryCompiledLibraryElm(libraryIdentifier, cqlCompilerOptions)
            if (result != null) {
                sortStatements(result)
                return result
            }
        }
        val libraryPath: String =
            NamespaceManager.getPath(libraryIdentifier.system, libraryIdentifier.id!!)
        try {
            val cqlSource: InputStream =
                librarySourceLoader.getLibrarySource(libraryIdentifier)
                    ?: throw CqlIncludeException(
                        String.format(
                            Locale.US,
                            "Could not load source for library %s, version %s.",
                            libraryIdentifier.id,
                            libraryIdentifier.version
                        ),
                        libraryIdentifier.system,
                        libraryIdentifier.id!!,
                        libraryIdentifier.version
                    )
            val compiler =
                CqlCompiler(
                    libraryIdentifier.system?.let { namespaceManager.getNamespaceInfoFromUri(it) },
                    libraryIdentifier,
                    this
                )
            compiler.run(cqlSource)
            errors?.addAll((compiler.exceptions)!!)
            result = compiler.compiledLibrary
            if (
                (libraryIdentifier.version != null &&
                    libraryIdentifier.version != result!!.identifier!!.version)
            ) {
                throw CqlIncludeException(
                    String.format(
                        Locale.US,
                        "Library %s was included as version %s, but version %s of the library was found.",
                        libraryPath,
                        libraryIdentifier.version,
                        result.identifier!!.version
                    ),
                    libraryIdentifier.system,
                    libraryIdentifier.id!!,
                    libraryIdentifier.version
                )
            }
        } catch (e: IOException) {
            throw CqlIncludeException(
                String.format(
                    Locale.US,
                    "Errors occurred translating library %s, version %s.",
                    libraryPath,
                    libraryIdentifier.version
                ),
                libraryIdentifier.system!!,
                libraryIdentifier.id!!,
                libraryIdentifier.version,
                e
            )
        }
        if (result == null) {
            throw CqlIncludeException(
                String.format(
                    Locale.US,
                    "Could not load source for library %s, version %s.",
                    libraryPath,
                    libraryIdentifier.version
                ),
                libraryIdentifier.system,
                libraryIdentifier.id!!,
                libraryIdentifier.version
            )
        } else {
            sortStatements(result)
            return result
        }
    }

    private fun sortStatements(compiledLibrary: CompiledLibrary?) {
        if (compiledLibrary == null || compiledLibrary.library!!.statements == null) {
            return
        }
        compiledLibrary.library!!.statements!!.def.sortBy { it.name }
    }

    private fun tryCompiledLibraryElm(
        libraryIdentifier: VersionedIdentifier,
        options: CqlCompilerOptions
    ): CompiledLibrary? {
        var elm: InputStream?
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
        librarySource: InputStream,
        type: LibraryContentType,
        @Suppress("UnusedParameter") options: CqlCompilerOptions
    ): CompiledLibrary? {
        var library: Library? = null
        var compiledLibrary: CompiledLibrary? = null
        try {
            library =
                ElmLibraryReaderFactory.getReader(type.mimeType())
                    .read(InputStreamReader(librarySource))
        } catch (@Suppress("SwallowedException") e: IOException) {
            // intentionally ignored
        }
        if (library != null && checkBinaryCompatibility(library)) {
            compiledLibrary = generateCompiledLibrary(library)
        }
        return compiledLibrary
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth", "ReturnCount")
    private fun generateCompiledLibrary(library: Library?): CompiledLibrary? {
        if (library == null) {
            return null
        }
        var compilationSuccess = true
        val compiledLibrary = CompiledLibrary()
        try {
            compiledLibrary.library = library
            if (library.identifier != null) {
                compiledLibrary.identifier = library.identifier
            }
            if (library.usings != null) {
                for (usingDef: UsingDef? in library.usings!!.def) {
                    compiledLibrary.add(usingDef!!)
                }
            }
            if (library.includes != null) {
                for (includeDef: IncludeDef? in library.includes!!.def) {
                    compiledLibrary.add(includeDef!!)
                }
            }
            if (library.codeSystems != null) {
                for (codeSystemDef: CodeSystemDef? in library.codeSystems!!.def) {
                    compiledLibrary.add(codeSystemDef!!)
                }
            }
            for (valueSetDef in library.valueSets!!.def) {
                compiledLibrary.add(valueSetDef)
            }
            if (library.codes != null) {
                for (codeDef: CodeDef? in library.codes!!.def) {
                    compiledLibrary.add(codeDef!!)
                }
            }
            if (library.concepts != null) {
                for (conceptDef: ConceptDef? in library.concepts!!.def) {
                    compiledLibrary.add(conceptDef!!)
                }
            }
            if (library.parameters != null) {
                for (parameterDef: ParameterDef? in library.parameters!!.def) {
                    compiledLibrary.add(parameterDef!!)
                }
            }
            if (library.statements != null) {
                for (expressionDef: ExpressionDef? in library.statements!!.def) {

                    // to do implement an ElmTypeInferencingVisitor; make sure that the resultType
                    // is set for each node
                    if (expressionDef!!.resultType != null) {
                        compiledLibrary.add(expressionDef)
                    } else {
                        compilationSuccess = false
                        break
                    }
                }
            }
        } catch (@Suppress("SwallowedException", "TooGenericExceptionCaught") e: Exception) {
            compilationSuccess = false
        }
        if (compilationSuccess) {
            return compiledLibrary
        }
        return null
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
        for (ed: ExpressionDef? in library.statements!!.def) {
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
    fun hasSignature(library: Library?): Boolean {
        if (library?.statements != null) {
            // Just a quick top-level scan for signatures. To fully verify we'd have to
            // recurse all
            // the way down. At that point, let's just translate.
            for (ed: ExpressionDef? in library.statements!!.def) {
                if (ed!!.expression is FunctionRef) {
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
        if (cqlCompilerOptions.compatibilityLevel.isNotBlank()) {
            val version: String? = CompilerOptions.getCompilerVersion(library)
            if (version != null) {
                return (version == cqlCompilerOptions.compatibilityLevel)
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
