package org.cqframework.cql.cql2elm

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.ModelInfoProvider
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@Suppress("PrintStackTrace", "UnusedPrivateProperty")
internal class ModelTests {
    @Test
    fun modelInfo() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    ModelTests::class
                        .java
                        .getResourceAsStream("ModelTests/ModelTest.cql")!!
                        .asSource()
                        .buffered(),
                    LibraryManager(modelManager!!),
                )
            val library = translator.toELM()
            assertThat(translator.errors.size, `is`(0))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private var modelManager: ModelManager? = null
        private var modelInfoProvider: ModelInfoProvider? = null

        @BeforeAll
        @JvmStatic
        fun setup() {
            modelManager = ModelManager()
            modelInfoProvider = TestModelInfoProvider()
            modelManager!!.modelInfoLoader.registerModelInfoProvider(modelInfoProvider!!)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            modelManager!!.modelInfoLoader.unregisterModelInfoProvider(modelInfoProvider!!)
        }
    }
}
