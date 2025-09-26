package org.cqframework.cql.elm

import java.io.IOException
import java.io.InputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.cql.model.NamespaceInfo

object TestUtils {
    @Throws(IOException::class)
    fun createTranslatorFromStream(
        testFileName: String,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        return createTranslatorFromStream(null, testFileName, *options)
    }

    @Throws(IOException::class)
    fun createTranslatorFromStream(
        namespaceInfo: NamespaceInfo?,
        testFileName: String,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        val inputStream = TestUtils::class.java.getResourceAsStream(testFileName)
        return createTranslatorFromStream(namespaceInfo, inputStream!!, *options)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun createTranslatorFromStream(
        inputStream: InputStream,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        return createTranslatorFromStream(null, inputStream, *options)
    }

    @Throws(IOException::class)
    fun createTranslatorFromStream(
        namespaceInfo: NamespaceInfo?,
        inputStream: InputStream,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        val modelManager = ModelManager()
        val compilerOptions = CqlCompilerOptions(*options)
        val libraryManager = LibraryManager(modelManager, compilerOptions)
        libraryManager.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        val translator =
            CqlTranslator.fromSource(
                namespaceInfo,
                inputStream.asSource().buffered(),
                libraryManager
            )
        return translator
    }
}
