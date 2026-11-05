package org.cqframework.cql.cql2elm

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@Suppress("PrintStackTrace")
internal class ElmSupportTest {
    @Test
    fun includedLibraryWithJsonElm() {
        val options =
            CqlCompilerOptions(
                CqlCompilerException.ErrorSeverity.Info,
                LibraryBuilder.SignatureLevel.All,
            )
        libraryManager = LibraryManager(modelManager!!, options)

        libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        try {
            val translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream("LibraryTests/ReferencingLibraryJsonElm.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )

            Assertions.assertTrue(translator.errors.isNotEmpty())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun includedLibraryWithXmlElm() {
        val options =
            CqlCompilerOptions(
                CqlCompilerException.ErrorSeverity.Info,
                LibraryBuilder.SignatureLevel.All,
            )
        libraryManager = LibraryManager(modelManager!!, options)
        libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())

        try {
            val translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream("LibraryTests/ReferencingLibraryXmlElm.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )

            Assertions.assertTrue(translator.errors.isNotEmpty())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun includedLibraryWithJsonWithNullTypeSpecifierElm() {
        val options =
            CqlCompilerOptions(
                CqlCompilerException.ErrorSeverity.Info,
                LibraryBuilder.SignatureLevel.All,
            )
        libraryManager = LibraryManager(modelManager!!, options)
        libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        try {
            val translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream(
                            "LibraryTests/ReferencingLibraryWithNullTypeSpecifierJsonElm.cql"
                        )!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )

            Assertions.assertTrue(translator.errors.isNotEmpty())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        var modelManager: ModelManager? = null
        var libraryManager: LibraryManager? = null

        @JvmStatic
        @BeforeAll
        fun setup() {
            modelManager = ModelManager()
            libraryManager = LibraryManager(modelManager!!)
            libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            libraryManager!!.librarySourceLoader.clearProviders()
        }
    }
}
