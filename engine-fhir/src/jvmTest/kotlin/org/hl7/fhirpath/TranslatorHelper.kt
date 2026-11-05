package org.hl7.fhirpath

import org.cqframework.cql.cql2elm.CqlCompiler
import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.StringLibrarySourceProvider
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider
import org.fhir.ucum.UcumException
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider

object TranslatorHelper {
    // Shared model cache — thread-safe (ConcurrentHashMap)
    private val globalModelCache =
        org.cqframework.cql.cql2elm.utils.createConcurrentHashMap<
            org.hl7.cql.model.ModelIdentifier,
            org.cqframework.cql.cql2elm.model.Model,
        >()

    private fun defaultCompilerOptions(): CqlCompilerOptions {
        val options = defaultOptions()
        options.options.remove(CqlCompilerOptions.Options.DisableListDemotion)
        options.options.remove(CqlCompilerOptions.Options.DisableListPromotion)
        options.options.add(CqlCompilerOptions.Options.EnableDateRangeOptimization)
        options.options.add(CqlCompilerOptions.Options.EnableResultTypes)
        return options
    }

    // Pre-compile FHIRHelpers and other shared libraries once, then reuse the cache.
    private val sharedLibraryCache:
        Map<VersionedIdentifier, org.cqframework.cql.cql2elm.model.CompiledLibrary> by lazy {
        val warmupManager = newLibraryManagerInternal(defaultCompilerOptions(), emptyMap())
        warmupManager.librarySourceLoader.registerProvider(
            StringLibrarySourceProvider(
                listOf(
                    "library Warmup using FHIR version '4.0.1' include FHIRHelpers version '4.0.1' define X: 1"
                )
            )
        )
        val compiler = CqlCompiler(libraryManager = warmupManager)
        compiler.run(
            "library Warmup using FHIR version '4.0.1' include FHIRHelpers version '4.0.1' define X: 1"
        )
        // Return only the shared dependency libraries (not the warmup library itself)
        warmupManager.compiledLibraries.filter { it.key.id != "Warmup" }
    }

    private fun newLibraryManagerInternal(
        cqlCompilerOptions: CqlCompilerOptions,
        precompiledLibraries:
            Map<VersionedIdentifier, org.cqframework.cql.cql2elm.model.CompiledLibrary>,
    ): LibraryManager {
        val modelManager = ModelManager(globalCache = globalModelCache)
        val libraryCache = HashMap(precompiledLibraries)
        val libraryManager =
            LibraryManager(modelManager, cqlCompilerOptions, libraryCache = libraryCache)
        libraryManager.librarySourceLoader.clearProviders()
        libraryManager.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        libraryManager.librarySourceLoader.registerProvider(FhirLibrarySourceProvider())
        return libraryManager
    }

    private fun newLibraryManager(): LibraryManager {
        return newLibraryManagerInternal(defaultCompilerOptions(), sharedLibraryCache)
    }

    @JvmStatic
    fun getEngine(cql: String): CqlEngine {
        return getEngine(getEnvironment(cql))
    }

    fun getEnvironment(cql: String): Environment {
        val env = getEnvironment(null as TerminologyProvider?)
        env.libraryManager!!
            .librarySourceLoader
            .registerProvider(StringLibrarySourceProvider(listOf(cql)))
        return env
    }

    val environment: Environment
        get() = getEnvironment(null as TerminologyProvider?)

    fun getEnvironment(terminologyProvider: TerminologyProvider?): Environment {
        return Environment(newLibraryManager(), null, terminologyProvider)
    }

    fun getEngine(environment: Environment?): CqlEngine {
        return CqlEngine(environment!!, mutableSetOf(CqlEngine.Options.EnableTypeChecking))
    }

    @JvmStatic
    fun toElmIdentifier(name: String?): VersionedIdentifier {
        return VersionedIdentifier().withId(name)
    }

    @JvmStatic
    fun toElmIdentifier(name: String?, version: String?): VersionedIdentifier {
        return VersionedIdentifier().withId(name).withVersion(version)
    }

    @Throws(UcumException::class)
    fun translate(cql: String?, libraryManager: LibraryManager): Library {
        val compiler = CqlCompiler(libraryManager = libraryManager)
        val lib = compiler.run(cql!!)

        libraryManager.compiledLibraries[lib.identifier!!] = compiler.compiledLibrary!!

        val errors = compiler.exceptions.filter { it.severity == ErrorSeverity.Error }
        if (errors.isNotEmpty()) {
            val messages = mutableListOf<String>()
            for (error in compiler.exceptions) {
                val tb = error.locator
                val lines =
                    if (tb == null) "[n/a]"
                    else
                        String.format(
                            "[%d:%d, %d:%d]",
                            tb.startLine,
                            tb.startChar,
                            tb.endLine,
                            tb.endChar,
                        )
                messages.add(lines + error.message)
            }
            throw IllegalArgumentException(messages.toString())
        }

        return lib
    }
}
