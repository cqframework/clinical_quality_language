package org.cqframework.cql.cql2elm

import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.OperatorExpression
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class CMS146SignatureTest {
    @ParameterizedTest
    @MethodSource("sigCounts")
    @Throws(IOException::class)
    fun cms146SignatureLevels(
        signatureLevel: LibraryBuilder.SignatureLevel,
        expectedSignatures: Int,
    ) {
        val cms146 = getFile("CMS146v2_Test_CQM.cql")
        val modelManager = ModelManager()
        val translator =
            CqlTranslator.fromFile(
                cms146.path,
                LibraryManager(
                    modelManager,
                    CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Warning, signatureLevel),
                ),
            )

        val visitor =
            FunctionalElmVisitor.from(
                { elm: Element?, _: Void? ->
                    if (elm is OperatorExpression) {
                        return@from if (elm.signature.isEmpty()) 0 else 1
                    } else {
                        return@from 0
                    }
                },
                { a, b -> a + b },
            )

        val sigCount = visitor.visitLibrary(translator.translatedLibrary!!.library!!, null)

        Assertions.assertEquals(expectedSignatures, sigCount)
    }

    companion object {
        // This is a count of the number of expected
        // signatures for each SignatureLevel when CMS146v2_Test_CQM.cql
        // is compiled.
        @Suppress("UnusedPrivateMember")
        @JvmStatic
        private fun sigCounts(): Array<Array<Any?>?> {
            return arrayOf(
                arrayOf(LibraryBuilder.SignatureLevel.None, 0),
                arrayOf(LibraryBuilder.SignatureLevel.Differing, 3),
                arrayOf(LibraryBuilder.SignatureLevel.Overloads, 11),
                arrayOf(LibraryBuilder.SignatureLevel.All, 34),
            )
        }

        private fun getFile(name: String): File {
            val resource = CMS146SignatureTest::class.java.getResource(name)

            requireNotNull(resource) { "Cannot find file with name: $name" }

            return File(URLDecoder.decode(resource.file, StandardCharsets.UTF_8))
        }
    }
}
