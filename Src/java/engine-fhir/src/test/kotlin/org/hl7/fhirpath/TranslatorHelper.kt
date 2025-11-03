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
    private val modelManager = ModelManager()
    private var libraryManager: LibraryManager? = null

    private fun getLibraryManager(): LibraryManager {
        val options = defaultOptions()
        options.options.remove(CqlCompilerOptions.Options.DisableListDemotion)
        options.options.remove(CqlCompilerOptions.Options.DisableListPromotion)
        options.options.add(CqlCompilerOptions.Options.EnableDateRangeOptimization)
        return getLibraryManager(options)
    }

    private fun getLibraryManager(cqlCompilerOptions: CqlCompilerOptions): LibraryManager {
        libraryManager = LibraryManager(modelManager, cqlCompilerOptions)
        libraryManager!!.librarySourceLoader.clearProviders()
        libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        libraryManager!!.librarySourceLoader.registerProvider(FhirLibrarySourceProvider())
        return libraryManager!!
    }

    @JvmStatic
    fun getEngine(cql: String): CqlEngine {
        return getEngine(getEnvironment(cql))
    }

    fun getEnvironment(cql: String): Environment {
        val env = getEnvironment(null as TerminologyProvider?)
        env.libraryManager!!.compiledLibraries.clear()
        env.libraryManager!!
            .librarySourceLoader
            .registerProvider(StringLibrarySourceProvider(listOf(cql)))
        return env
    }

    val environment: Environment
        get() = getEnvironment(null as TerminologyProvider?)

    fun getEnvironment(terminologyProvider: TerminologyProvider?): Environment {
        return Environment(getLibraryManager(), null, terminologyProvider)
    }

    fun getEngine(environment: Environment?): CqlEngine {
        return CqlEngine(environment!!)
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
