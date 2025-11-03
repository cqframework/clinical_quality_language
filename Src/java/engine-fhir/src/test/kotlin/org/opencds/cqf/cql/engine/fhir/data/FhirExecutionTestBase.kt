package org.opencds.cqf.cql.engine.fhir.data

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.io.IOException
import kotlinx.io.files.Path
import org.cqframework.cql.cql2elm.CqlCompiler
import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider
import org.fhir.ucum.UcumException
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.fhir.model.CachedDstu2FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.CachedDstu3FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.CachedR4FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.Dstu2FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver

abstract class FhirExecutionTestBase {
    val libraryManager: LibraryManager
        get() = Companion.libraryManager!!

    val modelManager: ModelManager
        get() = Companion.modelManager!!

    val environment: Environment
        get() = Environment(this.libraryManager)

    private var _engine: CqlEngine? = null
    val engine: CqlEngine
        get() {
            if (_engine == null) {
                _engine = CqlEngine(this.environment)
            }
            return _engine!!
        }

    var library: Library? = null

    @BeforeEach
    @Throws(IOException::class, UcumException::class)
    fun beforeEachTestMethod() {
        val fileName = this.javaClass.getSimpleName()
        if (library == null) {
            try {
                val cqlFile = Path(this.javaClass.getResource("$fileName.cql")!!.path)

                val compiler = CqlCompiler(libraryManager = libraryManager)

                val library = compiler.run(cqlFile)

                val errors = compiler.exceptions.filter { it.severity == ErrorSeverity.Error }
                if (errors.isNotEmpty()) {
                    System.err.println("Translation failed due to errors:")
                    val messages = mutableListOf<String>()
                    for (error in errors) {
                        val tb = error.locator
                        val lines =
                            if (tb == null) "[n/a]"
                            else "[${tb.startLine}:${tb.startChar}, ${tb.endLine}:${tb.endChar}]"
                        System.err.printf("%s %s%n", lines, error.message)
                        messages.add(lines + error.message)
                    }
                    throw IllegalArgumentException(messages.toString())
                }

                this.library = library
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private var libraryManager: LibraryManager? = null
        private var modelManager: ModelManager? = null

        @JvmStatic protected var dstu2ModelResolver: Dstu2FhirModelResolver? = null
        protected var dstu2RetrieveProvider: RestFhirRetrieveProvider? = null
        @JvmStatic protected var dstu2Provider: CompositeDataProvider? = null

        @JvmStatic protected var dstu3ModelResolver: Dstu3FhirModelResolver? = null
        @JvmStatic protected var dstu3RetrieveProvider: RestFhirRetrieveProvider? = null
        @JvmStatic protected var dstu3Provider: CompositeDataProvider? = null

        @JvmStatic protected var r4ModelResolver: R4FhirModelResolver? = null
        protected var r4RetrieveProvider: RestFhirRetrieveProvider? = null
        @JvmStatic protected var r4Provider: CompositeDataProvider? = null

        @JvmStatic
        // protected File xmlFile = null;
        @BeforeAll
        fun setup() {
            val dstu2Context = FhirContext.forCached(FhirVersionEnum.DSTU2)
            dstu2ModelResolver = CachedDstu2FhirModelResolver()
            dstu2RetrieveProvider =
                RestFhirRetrieveProvider(
                    SearchParameterResolver(dstu2Context),
                    dstu2ModelResolver!!,
                    dstu2Context.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu2"),
                )
            dstu2Provider = CompositeDataProvider(dstu2ModelResolver, dstu2RetrieveProvider)

            val dstu3Context = FhirContext.forCached(FhirVersionEnum.DSTU3)
            dstu3ModelResolver = CachedDstu3FhirModelResolver()
            dstu3RetrieveProvider =
                RestFhirRetrieveProvider(
                    SearchParameterResolver(dstu3Context),
                    dstu3ModelResolver!!,
                    dstu3Context.newRestfulGenericClient(
                        "http://measure.eval.kanvix.com/cqf-ruler/baseDstu3"
                    ),
                )
            dstu3Provider = CompositeDataProvider(dstu3ModelResolver, dstu3RetrieveProvider)

            val r4Context = FhirContext.forCached(FhirVersionEnum.R4)
            r4ModelResolver = CachedR4FhirModelResolver()
            r4RetrieveProvider =
                RestFhirRetrieveProvider(
                    SearchParameterResolver(r4Context),
                    r4ModelResolver!!,
                    r4Context.newRestfulGenericClient(
                        "http://measure.eval.kanvix.com/cqf-ruler/baseDstu4"
                    ),
                )
            r4Provider = CompositeDataProvider(r4ModelResolver, r4RetrieveProvider)

            modelManager = ModelManager()
            val compilerOptions = defaultOptions()
            libraryManager = LibraryManager(modelManager!!, compilerOptions)
            libraryManager!!.librarySourceLoader.clearProviders()
            libraryManager!!.librarySourceLoader.registerProvider(FhirLibrarySourceProvider())
            libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        }

        fun toElmIdentifier(name: String?): VersionedIdentifier {
            return VersionedIdentifier().withId(name)
        }

        fun toElmIdentifier(name: String?, version: String?): VersionedIdentifier {
            return VersionedIdentifier().withId(name).withVersion(version)
        }
    }
}
