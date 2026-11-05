package org.cqframework.cql.cql2elm

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.cql.model.NamespaceManager
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class ModelWithoutDefaultLoadersTests {
    @Test
    fun modelInfo() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    ModelWithoutDefaultLoadersTests::class
                        .java
                        .getResourceAsStream("ModelTests/ModelTest.cql")!!
                        .asSource()
                        .buffered(),
                    LibraryManager(modelManager!!),
                )
            translator.toELM()
            assertThat(translator.errors.size, `is`(0))
        } catch (e: IOException) {
            @Suppress("PrintStackTrace") e.printStackTrace()
        }
    }

    companion object {
        private var modelManager: ModelManager? = null
        private var modelInfoProvider: ModelInfoProvider? = null

        @JvmStatic
        @BeforeAll
        fun setup() {
            modelManager = ModelManager(NamespaceManager(), false)
            modelInfoProvider = TestModelInfoProvider()
            // modelManager.getModelInfoLoader().registerSystemModelInfoProvider();
            modelManager!!.modelInfoLoader.registerModelInfoProvider(modelInfoProvider!!)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            modelManager!!.modelInfoLoader.unregisterModelInfoProvider(modelInfoProvider!!)
        }
    }
}
