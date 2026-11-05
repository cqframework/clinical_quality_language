package org.cqframework.cql.cql2elm

import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessor
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// This test compiles a few example libraries and ensures
// local ids are assigned for all elements in the resulting ELM
internal class TestLocalId {
    @Test
    @Throws(Exception::class)
    fun localIds() {
        runTest("OperatorTests/CqlListOperators.cql")
        runTest("TranslationTests.cql")
        runTest("LibraryTests/TestMeasure.cql")
    }

    @Throws(Exception::class)
    private fun runTest(cqlFileName: String) {
        val lib =
            TestUtils.createTranslator(
                    cqlFileName,
                    CqlCompilerOptions.Options.EnableLocators,
                    CqlCompilerOptions.Options.EnableAnnotations,
                )
                .toELM()

        idChecker.visitLibrary(lib!!, cqlFileName)
    }

    @Test
    @Throws(Exception::class)
    fun noLocalIdThrowsException() {
        // This is an intentionally broken IdObjectFactory that will not assign localIds
        val brokenFactory =
            object : IdObjectFactory() {
                override fun nextId(): String {
                    return ""
                }
            }

        // A bit longer setup because we're handling some deeper internals
        val modelManager = ModelManager()
        val options =
            CqlCompilerOptions(
                CqlCompilerOptions.Options.EnableLocators,
                CqlCompilerOptions.Options.EnableAnnotations,
            )
        val libraryManager = LibraryManager(modelManager, options)
        libraryManager.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        val libraryBuilder = LibraryBuilder(libraryManager, brokenFactory)

        // Simplest possible library, just to trigger a missing id error.
        val lexer = cqlLexer(CharStreams.fromString("library Test\ndefine \"One\": 1"))
        val tokens = CommonTokenStream(lexer)
        val parser = cqlParser(tokens)
        parser.buildParseTree = true
        val tree = parser.library()
        val preprocessor = CqlPreprocessor(libraryBuilder, tokens)
        preprocessor.visit(tree)
        val visitor = Cql2ElmVisitor(libraryBuilder, tokens, preprocessor.libraryInfo)
        visitor.visit(tree)

        val exceptions = libraryBuilder.exceptions
        // Exceptions for the literal and the define, plus the library itself
        Assertions.assertEquals(3, exceptions.size)
        val e = exceptions[0]
        Assertions.assertTrue(e.message?.contains("localId") ?: false)
    }

    companion object {
        // This visitor checks that all nodes the graph have a localId
        var idChecker =
            FunctionalElmVisitor.from<String, Unit> { node, libraryName ->
                val trackbacks = node.trackbacks
                val locator: String? =
                    if (trackbacks.isEmpty()) "<unknown>" else trackbacks[0].toLocator()
                Assertions.assertNotNull(
                    node.localId,
                    """Element of type ${node.javaClass.simpleName}
                        | in library $libraryName at $locator has a null localId"""
                        .trimMargin(),
                )
                Assertions.assertFalse(
                    node.localId!!.isEmpty(),
                    """Element of type ${node.javaClass.simpleName}
                        | in library $libraryName at $locator has an empty localId"""
                        .trimMargin(),
                )
            }
    }
}
