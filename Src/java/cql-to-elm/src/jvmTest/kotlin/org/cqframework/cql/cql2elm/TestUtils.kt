package org.cqframework.cql.cql2elm

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Optional
import java.util.function.Supplier
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessor
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.Library

object TestUtils {
    private val modelManager: ModelManager
        get() = ModelManager()

    @Throws(IOException::class)
    @JvmStatic
    fun visitFile(fileName: String, inClassPath: Boolean): Cql2ElmVisitor {
        val `is` =
            if (inClassPath) TestUtils::class.java.getResourceAsStream(fileName)
            else FileInputStream(fileName)
        val tokens = parseCharStream(CharStreams.fromStream(`is`!!))
        val tree = parseTokenStream(tokens)
        val visitor = createElmTranslatorVisitor(tokens, tree)
        visitor.visit(tree)
        return visitor
    }

    @Throws(IOException::class)
    @JvmStatic
    fun visitFile(fileName: String): Any? {
        return visitFile(fileName, null)
    }

    @Throws(IOException::class)
    @JvmStatic
    fun visitFile(fileName: String, signatureLevel: LibraryBuilder.SignatureLevel?): Any? {
        val file = getFileOrThrow(fileName)
        val translator = CqlTranslator.fromFile(file.path, getLibraryManager(signatureLevel))
        ensureValid(translator)
        return translator.toObject()
    }

    @Throws(IOException::class)
    @JvmStatic
    fun visitFileLibrary(fileName: String): CompiledLibrary? {
        return visitFileLibrary(fileName, null)
    }

    @Throws(IOException::class)
    @JvmStatic
    fun visitFileLibrary(
        fileName: String,
        signatureLevel: LibraryBuilder.SignatureLevel?
    ): CompiledLibrary? {
        val file = getFileOrThrow(fileName)
        val translator = CqlTranslator.fromFile(file.path, getLibraryManager(signatureLevel))
        ensureValid(translator)
        return translator.translatedLibrary
    }

    @JvmStatic
    fun visitData(cqlData: String): Any? {
        val translator = CqlTranslator.fromText(cqlData, libraryManager)
        ensureValid(translator)
        return translator.toObject()
    }

    @JvmStatic
    fun visitLibrary(cqlLibrary: String): Library? {
        val translator = CqlTranslator.fromText(cqlLibrary, libraryManager)
        ensureValid(translator)
        return translator.toELM()
    }

    @JvmStatic
    fun visitData(
        cqlData: String,
        enableAnnotations: Boolean,
        enableDateRangeOptimization: Boolean
    ): Any? {
        val compilerOptions = CqlCompilerOptions()
        if (enableAnnotations) {
            compilerOptions.options.add(CqlCompilerOptions.Options.EnableAnnotations)
        }
        if (enableDateRangeOptimization) {
            compilerOptions.options.add(CqlCompilerOptions.Options.EnableDateRangeOptimization)
        }

        val translator = CqlTranslator.fromText(cqlData, getLibraryManager(compilerOptions))
        ensureValid(translator)
        return translator.toObject()
    }

    private fun ensureValid(translator: CqlTranslator) {
        val builder = StringBuilder()
        for (error in translator.errors) {
            builder.appendLine(error.message)
        }
        check(builder.isEmpty()) { builder.toString() }
    }

    private fun createElmTranslatorVisitor(tokens: TokenStream, tree: ParseTree): Cql2ElmVisitor {
        val modelManager = ModelManager()
        val libraryManager = getLibraryManager(modelManager, null)
        val libraryBuilder = LibraryBuilder(libraryManager, IdObjectFactory())
        val preprocessor = CqlPreprocessor(libraryBuilder, tokens)
        preprocessor.visit(tree)
        val visitor = Cql2ElmVisitor(libraryBuilder, tokens, preprocessor.libraryInfo)
        return visitor
    }

    private fun parseTokenStream(tokens: TokenStream): ParseTree {
        val parser = cqlParser(tokens)
        parser.buildParseTree = true
        return parser.library()
    }

    private fun parseCharStream(charStream: CharStream): TokenStream {
        val lexer = cqlLexer(charStream)
        return CommonTokenStream(lexer)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun runSemanticTest(
        testFileName: String,
        expectedErrors: Int,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        return runSemanticTest(null, testFileName, expectedErrors, *options)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun runSemanticTestNoErrors(
        testFileName: String,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        return runSemanticTest(null, testFileName, 0, *options)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun runSemanticTest(
        testFileName: String,
        expectedErrors: Int,
        signatureLevel: LibraryBuilder.SignatureLevel?,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        val cqlCompilerOptions = CqlCompilerOptions(*options)
        if (signatureLevel != null) {
            cqlCompilerOptions.signatureLevel = signatureLevel
        }
        return runSemanticTest(testFileName, expectedErrors, cqlCompilerOptions)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun runSemanticTest(
        testFileName: String,
        expectedErrors: Int,
        options: CqlCompilerOptions
    ): CqlTranslator {
        return runSemanticTest(null, testFileName, expectedErrors, options)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun runSemanticTest(
        namespaceInfo: NamespaceInfo?,
        testFileName: String,
        expectedErrors: Int,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        val cqlCompilerOptions = CqlCompilerOptions(*options)
        return runSemanticTest(namespaceInfo, testFileName, expectedErrors, cqlCompilerOptions)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun runSemanticTest(
        namespaceInfo: NamespaceInfo?,
        testFileName: String,
        expectedErrors: Int,
        signatureLevel: LibraryBuilder.SignatureLevel?,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        val cqlCompilerOptions = CqlCompilerOptions(*options)
        if (signatureLevel != null) {
            cqlCompilerOptions.signatureLevel = signatureLevel
        }
        return runSemanticTest(namespaceInfo, testFileName, expectedErrors, cqlCompilerOptions)
    }

    @Throws(IOException::class)
    fun runSemanticTest(
        namespaceInfo: NamespaceInfo?,
        testFileName: String,
        expectedErrors: Int,
        options: CqlCompilerOptions
    ): CqlTranslator {
        val translator = createTranslator(namespaceInfo, testFileName, options)
        for (error in translator.errors) {
            System.err.printf(
                "(%d,%d): %s%n",
                error.locator!!.startLine,
                error.locator!!.startChar,
                error.message
            )
        }
        // We want to defer asserting on errors to the unit test by passing -1
        if (expectedErrors != -1) {
            MatcherAssert.assertThat(
                translator.errors.toString(),
                translator.errors.size,
                Matchers.`is`(expectedErrors)
            )
        }
        return translator
    }

    @Throws(IOException::class)
    @JvmStatic
    fun runSemanticTestWithOrWithoutErrors(
        namespaceInfo: NamespaceInfo?,
        testFileName: String,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        val translator = createTranslator(namespaceInfo, testFileName, CqlCompilerOptions(*options))
        for (error in translator.errors) {
            System.err.printf(
                "(%d,%d): %s%n",
                error.locator!!.startLine,
                error.locator!!.startChar,
                error.message
            )
        }
        return translator
    }

    @JvmStatic
    fun createTranslatorFromText(
        cqlText: String,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        val libraryManager = getLibraryManager(*options)
        return CqlTranslator.fromText(cqlText, libraryManager)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun createTranslatorFromStream(
        testFileName: String,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        return createTranslatorFromStream(null, testFileName, null, *options)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun createTranslatorFromStream(
        testFileName: String,
        signatureLevel: LibraryBuilder.SignatureLevel?,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        return createTranslatorFromStream(null, testFileName, signatureLevel, *options)
    }

    @Throws(IOException::class)
    fun createTranslatorFromStream(
        namespaceInfo: NamespaceInfo?,
        testFileName: String,
        signatureLevel: LibraryBuilder.SignatureLevel?,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        val inputStream = Cql2ElmVisitorTest::class.java.getResourceAsStream(testFileName)
        if (inputStream == null) {
            throw FileNotFoundException("cannot find file with path: $testFileName")
        }
        return createTranslatorFromStream(namespaceInfo, inputStream, signatureLevel, *options)
    }

    @Throws(IOException::class)
    fun createTranslatorFromStream(
        namespaceInfo: NamespaceInfo?,
        inputStream: InputStream,
        signatureLevel: LibraryBuilder.SignatureLevel?,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        val modelManager = ModelManager()
        val compilerOptions = CqlCompilerOptions(*options)
        if (signatureLevel != null) {
            compilerOptions.signatureLevel = signatureLevel
        }
        val libraryManager = getLibraryManager(modelManager, compilerOptions)
        return CqlTranslator.fromSource(
            namespaceInfo,
            inputStream.asSource().buffered(),
            libraryManager
        )
    }

    private fun getLibraryManager(
        modelManager: ModelManager,
        compilerOptions: CqlCompilerOptions?
    ): LibraryManager {
        val options = compilerOptions ?: CqlCompilerOptions.defaultOptions()
        val libraryManager = LibraryManager(modelManager, options)
        libraryManager.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        return libraryManager
    }

    @JvmStatic
    @Throws(IOException::class)
    fun createTranslator(
        testFileName: String,
        vararg options: CqlCompilerOptions.Options
    ): CqlTranslator {
        return createTranslator(null, testFileName, CqlCompilerOptions(*options))
    }

    @JvmStatic
    @Throws(IOException::class)
    fun createTranslator(testFileName: String, options: CqlCompilerOptions): CqlTranslator {
        return createTranslator(null, testFileName, options)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getTranslator(
        cqlTestFile: String,
        nullableLibrarySourceProvider: String?,
        signatureLevel: LibraryBuilder.SignatureLevel
    ): CqlTranslator {
        val testFile = getFileOrThrow(cqlTestFile)
        val modelManager = ModelManager()

        val compilerOptions =
            CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, signatureLevel)

        val libraryManager =
            getLibraryManager(compilerOptions, modelManager, nullableLibrarySourceProvider)
        return CqlTranslator.fromFile(testFile.path, libraryManager)
    }

    @Throws(IOException::class)
    fun createTranslator(
        namespaceInfo: NamespaceInfo?,
        testFileName: String,
        options: CqlCompilerOptions
    ): CqlTranslator {
        val segments: Array<String?> =
            testFileName.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var path: String? = null
        if (segments.size > 1) {
            for (i in 0 ..< segments.size - 1) {
                if (path == null) {
                    path = segments[i]
                } else {
                    path += "/" + segments[i]
                }
            }
        }

        val translationTestFile = getFileOrThrow(testFileName)
        val modelManager = ModelManager()
        val libraryManager = getLibraryManager(options, modelManager, path)
        return CqlTranslator.fromFile(namespaceInfo, translationTestFile.path, libraryManager)
    }

    @Throws(FileNotFoundException::class)
    fun getFileOrThrow(fileName: String): File {
        val resource =
            Optional.ofNullable<URL>(Cql2ElmVisitorTest::class.java.getResource(fileName))
                .orElseThrow(
                    Supplier { FileNotFoundException("cannot find file with path: $fileName") }
                )
        return File(URLDecoder.decode(resource.file, StandardCharsets.UTF_8))
    }

    private val libraryManager: LibraryManager
        get() {
            val sig: LibraryBuilder.SignatureLevel? = null
            return getLibraryManager(sig)
        }

    private fun getLibraryManager(signatureLevel: LibraryBuilder.SignatureLevel?): LibraryManager {
        return getLibraryManager(
            CqlCompilerOptions(
                CqlCompilerException.ErrorSeverity.Warning,
                signatureLevel ?: LibraryBuilder.SignatureLevel.All
            )
        )
    }

    private fun getLibraryManager(options: CqlCompilerOptions): LibraryManager {
        return getLibraryManager(modelManager, options)
    }

    private fun getLibraryManager(vararg options: CqlCompilerOptions.Options): LibraryManager {
        val modelManager = ModelManager()
        val compilerOptions = CqlCompilerOptions(*options)
        return getLibraryManager(compilerOptions, modelManager, null)
    }

    private fun getLibraryManager(
        options: CqlCompilerOptions,
        modelManager: ModelManager,
        path: String?
    ): LibraryManager {
        val libraryManager = LibraryManager(modelManager, options)
        libraryManager.librarySourceLoader.registerProvider(
            if (path == null) TestLibrarySourceProvider() else TestLibrarySourceProvider(path)
        )
        return libraryManager
    }
}
