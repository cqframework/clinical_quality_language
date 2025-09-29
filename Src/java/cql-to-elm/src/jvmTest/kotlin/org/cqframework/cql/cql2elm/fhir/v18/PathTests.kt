package org.cqframework.cql.cql2elm.fhir.v18

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.TestFhirModelInfoProvider
import org.cqframework.cql.cql2elm.TestLibrarySourceProvider
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.ModelInfoProvider
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@Suppress("PrintStackTrace", "UnusedPrivateProperty", "MaxLineLength")
/** Created by Bryn on 12/11/2016. */
internal class PathTests {
    @Test
    fun paths() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    PathTests::class
                        .java
                        .getResourceAsStream("PathTests.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!
                )
            val library = translator.toELM()
            assertThat(translator.errors.size, `is`(0))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private var libraryManager: LibraryManager? = null
        private var modelManager: ModelManager? = null
        private var modelInfoProvider: ModelInfoProvider? = null

        @JvmStatic
        @BeforeAll
        fun setup() {
            modelManager = ModelManager()
            libraryManager = LibraryManager(modelManager!!)
            libraryManager!!.librarySourceLoader.clearProviders()
            libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
            libraryManager!!.librarySourceLoader.registerProvider(FhirLibrarySourceProvider())
            modelInfoProvider = TestFhirModelInfoProvider(PathTests::class.java)
            modelManager!!.modelInfoLoader.registerModelInfoProvider(modelInfoProvider!!, true)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            modelManager!!.modelInfoLoader.unregisterModelInfoProvider(modelInfoProvider!!)
        }
    }
}
