package org.opencds.cqf.cql.engine.fhir.data

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider
import org.junit.jupiter.api.BeforeAll
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

abstract class FhirExecutionMultiLibTestBase {
    val libraryManager: LibraryManager
        get() = Companion.libraryManager!!

    val modelManager: ModelManager
        get() = Companion.modelManager!!

    val environmentWithExistingLibraryManager: Environment
        get() = Environment(this.libraryManager)

    val environmentWithNewLibraryManager: Environment
        get() = Environment(buildNewLibraryManager())

    val engineWithExistingLibraryManager: CqlEngine
        get() = CqlEngine(this.environmentWithExistingLibraryManager)

    val engineWithNewLibraryManager: CqlEngine
        get() = CqlEngine(this.environmentWithNewLibraryManager)

    private fun buildNewLibraryManager(): LibraryManager {
        val libraryManagerInner = LibraryManager(ModelManager(), defaultOptions())

        libraryManagerInner.librarySourceLoader.clearProviders()
        libraryManagerInner.librarySourceLoader.registerProvider(FhirLibrarySourceProvider())
        libraryManagerInner.librarySourceLoader.registerProvider(TestLibrarySourceProvider())

        return libraryManagerInner
    }

    companion object {
        private var libraryManager: LibraryManager? = null
        private var modelManager: ModelManager? = null

        protected var dstu2ModelResolver: Dstu2FhirModelResolver? = null
        protected var dstu2RetrieveProvider: RestFhirRetrieveProvider? = null
        protected var dstu2Provider: CompositeDataProvider? = null

        protected var dstu3ModelResolver: Dstu3FhirModelResolver? = null
        protected var dstu3RetrieveProvider: RestFhirRetrieveProvider? = null
        protected var dstu3Provider: CompositeDataProvider? = null

        @JvmField protected var r4ModelResolver: R4FhirModelResolver? = null
        protected var r4RetrieveProvider: RestFhirRetrieveProvider? = null
        protected var r4Provider: CompositeDataProvider? = null

        // TODO: LD: figure out how to compile the CQLs only once for the whole test class
        @JvmStatic
        @BeforeAll
        fun setup() {
            val dstu2Context = FhirContext.forCached(FhirVersionEnum.DSTU2)
            dstu2ModelResolver = CachedDstu2FhirModelResolver()
            dstu2RetrieveProvider =
                RestFhirRetrieveProvider(
                    SearchParameterResolver(dstu2Context),
                    dstu2ModelResolver,
                    dstu2Context.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu2"),
                )
            dstu2Provider = CompositeDataProvider(dstu2ModelResolver, dstu2RetrieveProvider)

            val dstu3Context = FhirContext.forCached(FhirVersionEnum.DSTU3)
            dstu3ModelResolver = CachedDstu3FhirModelResolver()
            dstu3RetrieveProvider =
                RestFhirRetrieveProvider(
                    SearchParameterResolver(dstu3Context),
                    dstu3ModelResolver,
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
                    r4ModelResolver,
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
    }
}
